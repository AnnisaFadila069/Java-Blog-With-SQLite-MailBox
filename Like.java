import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Like {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Gunakan: java Like <email> <article_id>");
            return;
        }

        String likerEmail = args[0];
        int articleId;

        // Konversi article_id ke integer
        try {
            articleId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: ID artikel harus berupa angka.");
            return;
        }

        // Dapatkan timestamp saat ini
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String likedAt = now.format(formatter);

        Connection c = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db3.sqlite3");

            // Cek apakah pengguna sudah menyukai artikel ini sebelumnya
            String checkSql = "SELECT COUNT(*) FROM likes WHERE article_id = ? AND liker_email = ?";
            checkStmt = c.prepareStatement(checkSql);
            checkStmt.setInt(1, articleId);
            checkStmt.setString(2, likerEmail);
            rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("❌ Error: Kamu sudah menyukai artikel #" + articleId);
                return;
            }

            // Jika belum like, tambahkan ke database
            String insertSql = "INSERT INTO likes(article_id, liker_email, liked_at) VALUES (?, ?, ?)";
            insertStmt = c.prepareStatement(insertSql);
            insertStmt.setInt(1, articleId);
            insertStmt.setString(2, likerEmail);
            insertStmt.setString(3, likedAt);
            insertStmt.executeUpdate();

            System.out.println("✅ Berhasil menyukai artikel #" + articleId);

        } catch (Exception e) {
            System.err.println("❌ Terjadi kesalahan: " + e.getMessage());
        } finally {
            // Menutup koneksi
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (c != null) c.close();
            } catch (SQLException ex) {
                System.err.println("❌ Gagal menutup koneksi: " + ex.getMessage());
            }
        }
    }
}