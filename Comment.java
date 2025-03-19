import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Comment {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Gunakan: java Comment <email> <article_id> <comment_text>");
            return;
        }

        String commenterEmail = args[0];
        int articleId;

        // Konversi article_id ke integer
        try {
            articleId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: ID artikel harus berupa angka.");
            return;
        }

        // Gabungkan teks komentar dari argumen
        StringBuilder commentText = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            commentText.append(args[i]).append(" ");
        }

        // Dapatkan timestamp saat ini
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String commentedAt = now.format(formatter);

        Connection c = null;
        PreparedStatement stmt = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db3.sqlite3");

            // Query untuk menyimpan komentar
            String sql = "INSERT INTO comments(article_id, commenter_email, comment_text, commented_at) " +
                         "VALUES (?, ?, ?, ?)";
            stmt = c.prepareStatement(sql);
            stmt.setInt(1, articleId);
            stmt.setString(2, commenterEmail);
            stmt.setString(3, commentText.toString().trim());
            stmt.setString(4, commentedAt);
            stmt.executeUpdate();

            System.out.println("✅ Komentar berhasil ditambahkan pada artikel #" + articleId);

        } catch (Exception e) {
            System.err.println("❌ Terjadi kesalahan: " + e.getMessage());
        } finally {
            // Menutup koneksi
            try {
                if (stmt != null) stmt.close();
                if (c != null) c.close();
            } catch (SQLException ex) {
                System.err.println("❌ Gagal menutup koneksi: " + ex.getMessage());
            }
        }
    }
}