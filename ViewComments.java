import java.sql.*;

public class ViewComments {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Gunakan: java ViewComments <article_id>");
            return;
        }

        int articleId;
        try {
            articleId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: ID artikel harus berupa angka.");
            return;
        }

        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db3.sqlite3");

            // Cek apakah artikel ada
            String checkArticleSql = "SELECT title FROM articles WHERE id = ?";
            stmt = c.prepareStatement(checkArticleSql);
            stmt.setInt(1, articleId);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Artikel dengan ID " + articleId + " tidak ditemukan.");
                return;
            }

            String articleTitle = rs.getString("title");

            // Menampilkan komentar dari artikel
            String sql = "SELECT commenter_email, comment_text, commented_at FROM comments WHERE article_id = ? ORDER BY commented_at ASC";
            stmt = c.prepareStatement(sql);
            stmt.setInt(1, articleId);
            rs = stmt.executeQuery();

            System.out.println("=======================================");
            System.out.println("💬 Komentar untuk Artikel: " + articleTitle + " (ID: " + articleId + ")");
            System.out.println("=======================================");

            boolean hasComments = false;

            while (rs.next()) {
                hasComments = true;
                System.out.println("👤 " + rs.getString("commenter_email"));
                System.out.println("🕒 " + rs.getString("commented_at"));
                System.out.println("💭 " + rs.getString("comment_text"));
                System.out.println("---------------------------------------");
            }

            if (!hasComments) {
                System.out.println("❌ Belum ada komentar pada artikel ini.");
            }

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