import java.sql.*;

public class ViewArticle {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Gunakan: java ViewArticle <article_id> atau <author_email>");
            return;
        }

        String input = args[0];
        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Koneksi ke SQLite
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:db3.sqlite3");

            // Menentukan query berdasarkan input
            String sql;
            boolean isNumeric = input.matches("\\d+"); // Cek apakah input adalah angka (ID artikel)

            if (isNumeric) {
                // Jika input berupa angka, cari berdasarkan ID artikel
                sql = "SELECT id, author_email, created_at, title, content FROM articles WHERE id = ?";
                stmt = c.prepareStatement(sql);
                stmt.setInt(1, Integer.parseInt(input));
            } else {
                // Jika input berupa email, cari semua artikel dari penulis tersebut
                sql = "SELECT id, author_email, created_at, title, content FROM articles WHERE author_email = ? ORDER BY created_at DESC";
                stmt = c.prepareStatement(sql);
                stmt.setString(1, input);
            }

            // Eksekusi query
            rs = stmt.executeQuery();

            boolean hasResults = false;

            while (rs.next()) {
                hasResults = true;
                System.out.println("=======================================");
                System.out.println("📝 Artikel #" + rs.getInt("id"));
                System.out.println("✍️  Penulis  : " + rs.getString("author_email"));
                System.out.println("📅 Tanggal   : " + rs.getString("created_at"));
                System.out.println("📌 Judul     : " + rs.getString("title"));
                System.out.println("📖 Konten    :\n" + rs.getString("content"));
                System.out.println("=======================================\n");
            }

            if (!hasResults) {
                System.out.println("❌ Tidak ditemukan artikel untuk input: " + input);
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