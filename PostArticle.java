import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PostArticle {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java PostArticle <email> <title> <content>");
            return;
        }

        String email = args[0];
        String title = args[1];
        StringBuilder content = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            content.append(args[i]).append(" ");
        }

        // Ambil waktu saat ini
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        // Koneksi ke SQLite
        Class.forName("org.sqlite.JDBC");
        Connection c = DriverManager.getConnection("jdbc:sqlite:db3.sqlite3");

        // Query SQL untuk menyimpan artikel
        String insertArticleSql = "INSERT INTO articles(author_email, created_at, title, content) VALUES (?, ?, ?, ?)";
        PreparedStatement articleStmt = c.prepareStatement(insertArticleSql, Statement.RETURN_GENERATED_KEYS);
        articleStmt.setString(1, email);
        articleStmt.setString(2, formattedDate);
        articleStmt.setString(3, title);
        articleStmt.setString(4, content.toString().trim());
        articleStmt.executeUpdate();

        // Dapatkan ID artikel yang baru saja dibuat
        ResultSet generatedKeys = articleStmt.getGeneratedKeys();
        int articleId = -1;
        if (generatedKeys.next()) {
            articleId = generatedKeys.getInt(1);
        }
        generatedKeys.close();
        articleStmt.close();

        // Kirim artikel ke inbox pengguna yang sudah subscribe
        if (articleId != -1) {
            String insertInboxSql = "INSERT INTO inbox(receiver_email, article_id, received_at) " +
                        "SELECT subscriptions.subscriber_email, ?, ? FROM subscriptions WHERE blog_author_email = ?";
            PreparedStatement inboxStmt = c.prepareStatement(insertInboxSql);
            inboxStmt.setInt(1, articleId);
            inboxStmt.setString(2, formattedDate);
            inboxStmt.setString(3, email);
            inboxStmt.executeUpdate();
            inboxStmt.close();
        }

        // Tutup koneksi
        c.close();

        System.out.println("✅ Artikel berhasil diposting dan dikirim ke inbox subscriber!");
    }
}