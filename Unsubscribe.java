import java.sql.*;

public class Unsubscribe {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Gunakan: java Unsubscribe <subscriber_email> <blog_author_email>");
            return;
        }

        String subscriberEmail = args[0];
        String blogAuthorEmail = args[1];

        // Cek agar user tidak bisa unsubscribe dari dirinya sendiri
        if (subscriberEmail.equalsIgnoreCase(blogAuthorEmail)) {
            System.out.println("❌ Error: Kamu tidak bisa unsubscribe dari blog milikmu sendiri!");
            return;
        }

        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db3.sqlite3");

            // Cek apakah pengguna sudah berlangganan ke blog ini
            String checkSql = "SELECT COUNT(*) FROM subscriptions WHERE subscriber_email=? AND blog_author_email=?";
            stmt = c.prepareStatement(checkSql);
            stmt.setString(1, subscriberEmail);
            stmt.setString(2, blogAuthorEmail);
            rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("❌ Error: Kamu belum berlangganan ke blog " + blogAuthorEmail);
                return;
            }

            // Hapus langganan dari database
            String sql = "DELETE FROM subscriptions WHERE subscriber_email=? AND blog_author_email=?";
            stmt = c.prepareStatement(sql);
            stmt.setString(1, subscriberEmail);
            stmt.setString(2, blogAuthorEmail);
            stmt.executeUpdate();

            System.out.println("✅ Berhasil berhenti berlangganan dari blog " + blogAuthorEmail);

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