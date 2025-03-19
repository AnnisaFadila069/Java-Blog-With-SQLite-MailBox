import java.sql.*;

public class DeleteArticle {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Gunakan: java DeleteArticle <email_penulis> <article_id>");
            return;
        }

        String authorEmail = args[0];
        int articleId;

        try {
            articleId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: ID artikel harus berupa angka.");
            return;
        }

        Connection c = null;
        PreparedStatement stmt = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db3.sqlite3");

            // Cek apakah artikel tersebut memang milik penulis
            String checkSql = "SELECT COUNT(*) FROM articles WHERE id=? AND author_email=?";
            stmt = c.prepareStatement(checkSql);
            stmt.setInt(1, articleId);
            stmt.setString(2, authorEmail);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("❌ Error: Artikel dengan ID " + articleId + " tidak ditemukan atau bukan milik Anda.");
                return;
            }
            rs.close();
            stmt.close();

            // Hapus semua referensi terkait artikel
            String[] deleteQueries = {
                "DELETE FROM inbox WHERE article_id=?",
                "DELETE FROM comments WHERE article_id=?",
                "DELETE FROM likes WHERE article_id=?",
                "DELETE FROM saved_articles WHERE article_id=?",
                "DELETE FROM articles WHERE id=?"
            };

            for (String query : deleteQueries) {
                stmt = c.prepareStatement(query);
                stmt.setInt(1, articleId);
                stmt.executeUpdate();
                stmt.close();
            }

            System.out.println("✅ Artikel #" + articleId + " dan semua datanya telah dihapus.");

        } catch (Exception e) {
            System.err.println("❌ Terjadi kesalahan: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (c != null) c.close();
            } catch (SQLException ex) {
                System.err.println("❌ Gagal menutup koneksi: " + ex.getMessage());
            }
        }
    }
}