import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveArticle {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Gunakan: java SaveArticle <email> <article_id>");
            return;
        }

        String userEmail = args[0];
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
        String savedAt = now.format(formatter);

        Connection c = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db3.sqlite3");

            // Cek apakah pengguna sudah menyimpan artikel ini sebelumnya
            String checkSql = "SELECT COUNT(*) FROM saved_articles WHERE user_email = ? AND article_id = ?";
            checkStmt = c.prepareStatement(checkSql);
            checkStmt.setString(1, userEmail);
            checkStmt.setInt(2, articleId);
            rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("❌ Error: Kamu sudah menyimpan artikel #" + articleId);
                return;
            }

            // Jika belum, simpan artikel ke tabel saved_articles
            String insertSql = "INSERT INTO saved_articles(user_email, article_id, saved_at) VALUES (?, ?, ?)";
            insertStmt = c.prepareStatement(insertSql);
            insertStmt.setString(1, userEmail);
            insertStmt.setInt(2, articleId);
            insertStmt.setString(3, savedAt);
            insertStmt.executeUpdate();

            System.out.println("✅ Artikel #" + articleId + " berhasil disimpan!");

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