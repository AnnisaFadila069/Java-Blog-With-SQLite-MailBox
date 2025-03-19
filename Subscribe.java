import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subscribe {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Gunakan: java Subscribe <subscriber_email> <blog_author_email>");
            return;
        }

        String subscriberEmail = args[0];
        String blogAuthorEmail = args[1];

        // Cek agar user tidak bisa subscribe ke blog miliknya sendiri
        if (subscriberEmail.equalsIgnoreCase(blogAuthorEmail)) {
            System.out.println("❌ Error: Kamu tidak bisa subscribe ke blog milikmu sendiri!");
            return;
        }

        // Ambil waktu sekarang
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db3.sqlite3");

            // Cek apakah sudah berlangganan sebelumnya
            String checkSql = "SELECT COUNT(*) FROM subscriptions WHERE subscriber_email=? AND blog_author_email=?";
            stmt = c.prepareStatement(checkSql);
            stmt.setString(1, subscriberEmail);
            stmt.setString(2, blogAuthorEmail);
            rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("❌ Error: Kamu sudah berlangganan ke blog " + blogAuthorEmail);
                return;
            }

            // Insert data ke tabel subscriptions
            String sql = "INSERT INTO subscriptions(subscriber_email, blog_author_email, subscribed_at) VALUES(?, ?, ?)";
            stmt = c.prepareStatement(sql);
            stmt.setString(1, subscriberEmail);
            stmt.setString(2, blogAuthorEmail);
            stmt.setString(3, formattedDate);
            stmt.executeUpdate();

            System.out.println("✅ Berhasil berlangganan ke blog " + blogAuthorEmail);

        } catch (Exception e) {
            System.err.println("❌ Terjadi kesalahan: " + e.getMessage());
        } finally {
            // Menutup koneksi
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (c != null) c.close();
            } catch (SQLException ex) {
                System.err.println("❌ Gagal menutup koneksi: " + ex.getMessage());
            }
        }
    }
}