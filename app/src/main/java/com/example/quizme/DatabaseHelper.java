package com.example.quizme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "QuizAppDB";
    private static final int DATABASE_VERSION = 13; // Tăng version để cập nhật avatar

    // Bảng Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_COINS = "coins";
    public static final String COLUMN_REFER_CODE = "refer_code";
    public static final String COLUMN_PROFILE = "profile";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_AVATAR_IMAGE = "avatar_image";

    // Bảng Categories
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_CATEGORY_IMAGE = "category_image";

    // Bảng Questions
    public static final String TABLE_QUESTIONS = "questions";
    public static final String COLUMN_QUESTION_ID = "question_id";
    public static final String COLUMN_QUESTION_TEXT = "question_text";
    public static final String COLUMN_OPTION1 = "option1";
    public static final String COLUMN_OPTION2 = "option2";
    public static final String COLUMN_OPTION3 = "option3";
    public static final String COLUMN_OPTION4 = "option4";
    public static final String COLUMN_ANSWER = "answer";
    public static final String COLUMN_QUESTION_INDEX = "question_index";

    // Bảng Withdraws
    public static final String TABLE_WITHDRAWS = "withdraws";
    public static final String COLUMN_WITHDRAW_ID = "withdraw_id";
    public static final String COLUMN_EMAIL_ADDRESS = "email_address";
    public static final String COLUMN_REQUESTED_BY = "requested_by";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Bảng Quiz Results
    public static final String TABLE_QUIZ_RESULTS = "quiz_results";
    public static final String COLUMN_RESULT_ID = "result_id";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_TOTAL_QUESTIONS = "total_questions";
    public static final String COLUMN_CORRECT_ANSWERS = "correct_answers";
    public static final String COLUMN_COMPLETION_TIME = "completion_time";
    public static final String COLUMN_COMPLETED_AT = "completed_at";

    // Bảng Friend Requests (Lời mời kết bạn)
    public static final String TABLE_FRIEND_REQUESTS = "friend_requests";
    public static final String COLUMN_REQUEST_ID = "request_id";
    public static final String COLUMN_SENDER_ID = "sender_id";
    public static final String COLUMN_RECEIVER_ID = "receiver_id";
    public static final String COLUMN_STATUS = "status"; // pending, accepted, rejected
    public static final String COLUMN_REQUEST_DATE = "request_date";
    public static final String COLUMN_RESPONSE_DATE = "response_date";

    // Bảng Friends (Danh sách bạn bè)
    public static final String TABLE_FRIENDS = "friends";
    public static final String COLUMN_FRIENDSHIP_ID = "friendship_id";
    public static final String COLUMN_USER1_ID = "user1_id";
    public static final String COLUMN_USER2_ID = "user2_id";
    public static final String COLUMN_FRIENDSHIP_DATE = "friendship_date";

    // Bảng Messages (Tin nhắn)
    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "message_id";
    public static final String COLUMN_SENDER_USER_ID = "sender_user_id";
    public static final String COLUMN_RECEIVER_USER_ID = "receiver_user_id";
    public static final String COLUMN_MESSAGE_CONTENT = "message_content";
    public static final String COLUMN_MESSAGE_DATE = "message_date";
    public static final String COLUMN_IS_READ = "is_read"; // 0 = chưa đọc, 1 = đã đọc

    // Tạo bảng Users
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
            COLUMN_PASSWORD + " TEXT NOT NULL, " +
            COLUMN_ROLE + " TEXT DEFAULT 'user', " +
            COLUMN_COINS + " INTEGER DEFAULT 25, " +
            COLUMN_REFER_CODE + " TEXT, " +
            COLUMN_PROFILE + " TEXT, " +
            COLUMN_PHONE + " TEXT, " +
            COLUMN_AVATAR_IMAGE + " TEXT)";

    // Tạo bảng Categories
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
            COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CATEGORY_NAME + " TEXT NOT NULL, " +
            COLUMN_CATEGORY_IMAGE + " TEXT)";

    // Tạo bảng Questions
    private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE " + TABLE_QUESTIONS + " (" +
            COLUMN_QUESTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CATEGORY_ID + " INTEGER, " +
            COLUMN_QUESTION_TEXT + " TEXT NOT NULL, " +
            COLUMN_OPTION1 + " TEXT NOT NULL, " +
            COLUMN_OPTION2 + " TEXT NOT NULL, " +
            COLUMN_OPTION3 + " TEXT NOT NULL, " +
            COLUMN_OPTION4 + " TEXT NOT NULL, " +
            COLUMN_ANSWER + " TEXT NOT NULL, " +
            COLUMN_QUESTION_INDEX + " INTEGER, " +
            "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + "))";

    // Tạo bảng Withdraws
    private static final String CREATE_TABLE_WITHDRAWS = "CREATE TABLE " + TABLE_WITHDRAWS + " (" +
            COLUMN_WITHDRAW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER_ID + " INTEGER, " +
            COLUMN_EMAIL_ADDRESS + " TEXT NOT NULL, " +
            COLUMN_REQUESTED_BY + " TEXT NOT NULL, " +
            COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    // Tạo bảng Quiz Results
    private static final String CREATE_TABLE_QUIZ_RESULTS = "CREATE TABLE " + TABLE_QUIZ_RESULTS + " (" +
            COLUMN_RESULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER_ID + " INTEGER, " +
            COLUMN_CATEGORY_ID + " INTEGER, " +
            COLUMN_SCORE + " INTEGER NOT NULL, " +
            COLUMN_TOTAL_QUESTIONS + " INTEGER NOT NULL, " +
            COLUMN_CORRECT_ANSWERS + " INTEGER NOT NULL, " +
            COLUMN_COMPLETION_TIME + " INTEGER NOT NULL, " +
            COLUMN_COMPLETED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
            "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        System.out.println("DatabaseHelper initialized");
        // Đảm bảo database được tạo
        try {
            getWritableDatabase();
            System.out.println("Database created/opened successfully");
        } catch (Exception e) {
            System.out.println("Error creating database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            System.out.println("Creating database tables...");
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_TABLE_CATEGORIES);
            db.execSQL(CREATE_TABLE_QUESTIONS);
            db.execSQL(CREATE_TABLE_WITHDRAWS);
            db.execSQL(CREATE_TABLE_QUIZ_RESULTS);
            
            // Tạo các bảng mới cho hệ thống kết bạn và chat
            System.out.println("Creating friend system tables...");
            db.execSQL(CREATE_TABLE_FRIEND_REQUESTS);
            db.execSQL(CREATE_TABLE_FRIENDS);
            db.execSQL(CREATE_TABLE_MESSAGES);
            
            System.out.println("Creating default admin...");
            // Tạo admin mặc định
            createDefaultAdmin(db);
            
            System.out.println("Creating sample data...");
            // Tạo dữ liệu mẫu
            createSampleData(db);
            
            System.out.println("Database created successfully");
        } catch (Exception e) {
            System.out.println("Error creating database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop các table theo thứ tự ngược với foreign key
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIENDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRIEND_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WITHDRAWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void createDefaultAdmin(SQLiteDatabase db) {
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, "Quản trị viên");
            values.put(COLUMN_EMAIL, "admin@quizapp.com");
            values.put(COLUMN_PASSWORD, "admin123");
            values.put(COLUMN_ROLE, "admin");
            values.put(COLUMN_COINS, 1000);
            long result = db.insert(TABLE_USERS, null, values);
            if (result != -1) {
                System.out.println("Default admin created successfully");
            } else {
                System.out.println("Failed to create default admin");
            }
        } catch (Exception e) {
            System.out.println("Error creating default admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createSampleData(SQLiteDatabase db) {
        try {
            // Thêm categories mẫu
            ContentValues cat1 = new ContentValues();
            cat1.put(COLUMN_CATEGORY_NAME, "Lịch sử Việt Nam");
            cat1.put(COLUMN_CATEGORY_IMAGE, "lichsu");
            long catId1 = db.insert(TABLE_CATEGORIES, null, cat1);
            System.out.println("Category 1 created with ID: " + catId1);

            ContentValues cat2 = new ContentValues();
            cat2.put(COLUMN_CATEGORY_NAME, "Địa lý Việt Nam");
            cat2.put(COLUMN_CATEGORY_IMAGE, "dialy");
            long catId2 = db.insert(TABLE_CATEGORIES, null, cat2);
            System.out.println("Category 2 created with ID: " + catId2);

            ContentValues cat3 = new ContentValues();
            cat3.put(COLUMN_CATEGORY_NAME, "Văn hóa Việt Nam");
            cat3.put(COLUMN_CATEGORY_IMAGE, "culture");
            long catId3 = db.insert(TABLE_CATEGORIES, null, cat3);
            System.out.println("Category 3 created with ID: " + catId3);

            ContentValues cat4 = new ContentValues();
            cat4.put(COLUMN_CATEGORY_NAME, "Khoa học tự nhiên");
            cat4.put(COLUMN_CATEGORY_IMAGE, "nature");
            long catId4 = db.insert(TABLE_CATEGORIES, null, cat4);
            System.out.println("Category 4 created with ID: " + catId4);

            ContentValues cat5 = new ContentValues();
            cat5.put(COLUMN_CATEGORY_NAME, "Toán học");
            cat5.put(COLUMN_CATEGORY_IMAGE, "math");
            long catId5 = db.insert(TABLE_CATEGORIES, null, cat5);
            System.out.println("Category 5 created with ID: " + catId5);

            // Thêm questions mẫu cho Lịch sử Việt Nam
            addSampleQuestion(db, catId1, "Ai là vị vua đầu tiên của nước Việt Nam?", 
                "Lý Thái Tổ", "Lê Lợi", "Quang Trung", "Hồ Chí Minh", "Lý Thái Tổ", 1);
            addSampleQuestion(db, catId1, "Năm nào nước Việt Nam giành độc lập từ Pháp?", 
                "1945", "1954", "1975", "1986", "1945", 2);
            addSampleQuestion(db, catId1, "Ai là người lãnh đạo cuộc khởi nghĩa Lam Sơn?", 
                "Lý Thường Kiệt", "Lê Lợi", "Nguyễn Huệ", "Trần Hưng Đạo", "Lê Lợi", 3);
            addSampleQuestion(db, catId1, "Triều đại nào kéo dài nhất trong lịch sử Việt Nam?", 
                "Nhà Lý", "Nhà Trần", "Nhà Lê", "Nhà Nguyễn", "Nhà Lê", 4);
            addSampleQuestion(db, catId1, "Ai là người sáng lập ra nhà Hậu Lê?", 
                "Lê Lợi", "Lê Thánh Tông", "Lê Thái Tổ", "Lê Hiển Tông", "Lê Lợi", 5);
            addSampleQuestion(db, catId1, "Cuộc kháng chiến chống Pháp bắt đầu vào năm nào?", 
                "1945", "1946", "1947", "1948", "1946", 6);
            addSampleQuestion(db, catId1, "Ai là hoàng đế cuối cùng của nhà Nguyễn?", 
                "Bảo Đại", "Duy Tân", "Khải Định", "Thành Thái", "Bảo Đại", 7);
            addSampleQuestion(db, catId1, "Trận Bạch Đằng lần thứ nhất diễn ra vào năm nào?", 
                "938", "981", "1288", "1427", "938", 8);
            addSampleQuestion(db, catId1, "Ai là người chỉ huy trận Điện Biên Phủ?", 
                "Võ Nguyên Giáp", "Nguyễn Chí Thanh", "Văn Tiến Dũng", "Hoàng Văn Thái", "Võ Nguyên Giáp", 9);
            addSampleQuestion(db, catId1, "Nhà Trần tồn tại từ năm nào đến năm nào?", 
                "1225-1400", "1226-1413", "1225-1413", "1226-1400", "1226-1400", 10);
            addSampleQuestion(db, catId1, "Ai là vua sáng lập nhà Lý?", 
                "Lý Công Uẩn", "Lý Thái Tổ", "Lý Thường Kiệt", "Lý Thánh Tông", "Lý Công Uẩn", 11);
            addSampleQuestion(db, catId1, "Cuộc Nam tiến hoàn thành vào thời nào?", 
                "Thế kỷ 17", "Thế kỷ 18", "Thế kỷ 19", "Thế kỷ 16", "Thế kỷ 19", 12);
            addSampleQuestion(db, catId1, "Ai là người lãnh đạo khởi nghĩa Tây Sơn?", 
                "Nguyễn Nhạc", "Nguyễn Huệ", "Nguyễn Lữ", "Nguyễn Ánh", "Nguyễn Nhạc", 13);
            addSampleQuestion(db, catId1, "Thủ đô đầu tiên của nước Việt Nam thống nhất là gì?", 
                "Hoa Lư", "Thăng Long", "Phú Xuân", "Gia Định", "Hoa Lư", 14);
            addSampleQuestion(db, catId1, "Hiệp định Geneva được ký vào năm nào?", 
                "1954", "1955", "1956", "1953", "1954", 15);

            // Thêm questions mẫu cho Địa lý Việt Nam
            addSampleQuestion(db, catId2, "Thủ đô của Việt Nam là gì?", 
                "Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Huế", "Hà Nội", 6);
            addSampleQuestion(db, catId2, "Sông nào dài nhất Việt Nam?", 
                "Sông Hồng", "Sông Mekong", "Sông Đồng Nai", "Sông Hương", "Sông Mekong", 7);
            addSampleQuestion(db, catId2, "Núi nào cao nhất Việt Nam?", 
                "Núi Bà Đen", "Fansipan", "Núi Ngự Bình", "Núi Bà Rá", "Fansipan", 8);
            addSampleQuestion(db, catId2, "Việt Nam có bao nhiêu tỉnh thành?", 
                "61", "62", "63", "64", "63", 9);
            addSampleQuestion(db, catId2, "Đảo lớn nhất Việt Nam là đảo nào?", 
                "Phú Quốc", "Côn Đảo", "Cát Bà", "Lý Sơn", "Phú Quốc", 10);
            addSampleQuestion(db, catId2, "Việt Nam có bờ biển dài bao nhiêu km?", 
                "3260", "3444", "3600", "3800", "3444", 11);
            addSampleQuestion(db, catId2, "Sân bay lớn nhất Việt Nam là sân bay nào?", 
                "Nội Bài", "Tân Sơn Nhất", "Đà Nẵng", "Cam Ranh", "Tân Sơn Nhất", 12);
            addSampleQuestion(db, catId2, "Việt Nam giáp biên với bao nhiều nước?", 
                "2", "3", "4", "5", "3", 13);
            addSampleQuestion(db, catId2, "Thành phố nào được gọi là 'Paris của Đông Dương'?", 
                "Hà Nội", "Sài Gòn", "Đà Lạt", "Huế", "Sài Gòn", 14);
            addSampleQuestion(db, catId2, "Vùng kinh tế trọng điểm phía Nam gồm mấy tỉnh thành?", 
                "7", "8", "9", "10", "8", 15);
            addSampleQuestion(db, catId2, "Dãy núi dài nhất Việt Nam là dãy núi nào?", 
                "Trường Sơn", "Hoàng Liên Sơn", "Tây Nguyên", "Đông Triều", "Trường Sơn", 16);
            addSampleQuestion(db, catId2, "Hồ nước ngọt lớn nhất Việt Nam là hồ nào?", 
                "Hồ Tây", "Hồ Ba Bể", "Hồ Trị An", "Hồ Dầu Tiếng", "Hồ Dầu Tiếng", 17);
            addSampleQuestion(db, catId2, "Cực Nam của Việt Nam thuộc tỉnh nào?", 
                "Kiên Giang", "An Giang", "Cà Mau", "Bạc Liêu", "Cà Mau", 18);
            addSampleQuestion(db, catId2, "Việt Nam có bao nhiều quần đảo?", 
                "2", "3", "4", "5", "2", 19);
            addSampleQuestion(db, catId2, "Đèo cao nhất Việt Nam là đèo nào?", 
                "Đèo Hải Vân", "Đèo Ô Quý Hồ", "Đèo Khau Phạ", "Đèo Lao Than", "Đèo Khau Phạ", 20);

            // Thêm questions mẫu cho Văn hóa Việt Nam
            addSampleQuestion(db, catId3, "Tết Nguyên Đán thường diễn ra vào tháng nào?", 
                "Tháng 12", "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 1", 11);
            addSampleQuestion(db, catId3, "Món ăn nào được coi là quốc phẩm của Việt Nam?", 
                "Phở", "Bún chả", "Bánh mì", "Cơm tấm", "Phở", 12);
            addSampleQuestion(db, catId3, "Loại nhạc cụ truyền thống nào có 16 dây?", 
                "Đàn bầu", "Đàn tranh", "Đàn nguyệt", "Đàn tỳ bà", "Đàn tranh", 13);
            addSampleQuestion(db, catId3, "Trang phục truyền thống của phụ nữ Việt Nam là gì?", 
                "Áo dài", "Áo tứ thân", "Áo bà ba", "Áo yếm", "Áo dài", 14);
            addSampleQuestion(db, catId3, "Lễ hội nào lớn nhất ở Hà Nội?", 
                "Lễ hội chùa Hương", "Lễ hội đền Ngọc Sơn", "Lễ hội Tây Hồ", "Lễ hội Thăng Long", "Lễ hội chùa Hương", 15);
            addSampleQuestion(db, catId3, "Ca trù được UNESCO công nhận là di sản văn hóa vào năm nào?", 
                "2009", "2010", "2008", "2011", "2009", 16);
            addSampleQuestion(db, catId3, "Món ăn nào có tên gọi là 'bánh chưng nước'?", 
                "Bánh tét", "Bánh ít", "Bánh bèo", "Bánh nậm", "Bánh tét", 17);
            addSampleQuestion(db, catId3, "Loại hình nghệ thuật nào được gọi là 'opera Việt Nam'?", 
                "Chèo", "Tuồng", "Cải lương", "Xẩm", "Tuồng", 18);
            addSampleQuestion(db, catId3, "Lễ hội Đền Hùng tổ chức vào ngày nào?", 
                "10/3 âm lịch", "15/3 âm lịch", "20/3 âm lịch", "25/3 âm lịch", "10/3 âm lịch", 19);
            addSampleQuestion(db, catId3, "Nghề thủ công truyền thống nào nổi tiếng ở Bát Tràng?", 
                "Làm gốm sứ", "Dệt lụa", "Chạm khắc", "Đúc đồng", "Làm gốm sứ", 20);
            addSampleQuestion(db, catId3, "Câu ca dao nào nói về tình mẫu tử?", 
                "Công cha như núi Thái Sơn", "Lá tía tô có mấy cạnh", "Dòng sông nào chẳng có khúc", "Gần mực thì đen", "Công cha như núi Thái Sơn", 21);
            addSampleQuestion(db, catId3, "Múa rối nước Việt Nam bắt nguồn từ đâu?", 
                "Miền Bắc", "Miền Trung", "Miền Nam", "Tây Nguyên", "Miền Bắc", 22);
            addSampleQuestion(db, catId3, "Loại bánh nào thường ăn vào ngày Tết Trung thu?", 
                "Bánh chưng", "Bánh dày", "Bánh trung thu", "Bánh tét", "Bánh trung thu", 23);
            addSampleQuestion(db, catId3, "Trang phục truyền thống của đàn ông Việt Nam là gì?", 
                "Áo gấm", "Áo dài nam", "Áo tấc", "Áo the", "Áo dài nam", 24);
            addSampleQuestion(db, catId3, "Trung thu còn được gọi là tết gì?", 
                "Tết thiếu nhi", "Tết trăng rằm", "Tết cúng trăng", "Tất cả đều đúng", "Tất cả đều đúng", 25);

            // Thêm questions mẫu cho Khoa học tự nhiên
            addSampleQuestion(db, catId4, "Nguyên tố nào phổ biến nhất trong vũ trụ?", 
                "Heli", "Hydro", "Oxy", "Carbon", "Hydro", 16);
            addSampleQuestion(db, catId4, "Nước sôi ở nhiệt độ bao nhiêu độ C?", 
                "90°C", "100°C", "110°C", "120°C", "100°C", 17);
            addSampleQuestion(db, catId4, "Hành tinh nào gần Mặt Trời nhất?", 
                "Sao Thủy", "Sao Kim", "Trái Đất", "Sao Hỏa", "Sao Thủy", 18);
            addSampleQuestion(db, catId4, "Cơ thể người có bao nhiêu xương?", 
                "206", "216", "226", "236", "206", 19);
            addSampleQuestion(db, catId4, "Chất nào cần thiết cho quá trình quang hợp?", 
                "Oxy", "Carbon dioxide", "Nitơ", "Hydro", "Carbon dioxide", 20);
            addSampleQuestion(db, catId4, "Tốc độ ánh sáng trong chân không là bao nhiêu?", 
                "300.000 km/s", "150.000 km/s", "450.000 km/s", "200.000 km/s", "300.000 km/s", 21);
            addSampleQuestion(db, catId4, "Nguyên tố nào có ký hiệu hóa học là Au?", 
                "Bạc", "Vàng", "Đồng", "Kẽm", "Vàng", 22);
            addSampleQuestion(db, catId4, "Bao nhiêu nhiễm sắc thể trong tế bào người bình thường?", 
                "44", "46", "48", "50", "46", 23);
            addSampleQuestion(db, catId4, "Hành tinh nào có khối lượng lớn nhất trong hệ Mặt Trời?", 
                "Sao Thổ", "Sao Mộc", "Sao Hải Vương", "Trái Đất", "Sao Mộc", 24);
            addSampleQuestion(db, catId4, "Chất nào tạo màu xanh cho máu ở một số động vật?", 
                "Hemoglobin", "Hemocyanin", "Chlorophyll", "Melanin", "Hemocyanin", 25);
            addSampleQuestion(db, catId4, "Đơn vị đo cường độ dòng điện là gì?", 
                "Volt", "Ohm", "Ampere", "Watt", "Ampere", 26);
            addSampleQuestion(db, catId4, "Loại tia nào có bước sóng ngắn nhất?", 
                "Tia gamma", "Tia X", "Tia hồng ngoại", "Tia tử ngoại", "Tia gamma", 27);
            addSampleQuestion(db, catId4, "Khí nào chiếm tỷ lệ cao nhất trong khí quyển?", 
                "Oxy", "Nitơ", "Carbon dioxide", "Argon", "Nitơ", 28);
            addSampleQuestion(db, catId4, "Quá trình nào biến đổi năng lượng ánh sáng thành năng lượng hóa học?", 
                "Hô hấp", "Quang hợp", "Lên men", "Tiêu hóa", "Quang hợp", 29);
            addSampleQuestion(db, catId4, "Nguyên tố nào có số hiệu nguyên tử là 1?", 
                "Helium", "Hydrogen", "Lithium", "Carbon", "Hydrogen", 30);

            // Thêm questions mẫu cho Toán học
            addSampleQuestion(db, catId5, "Kết quả của 15 + 27 là bao nhiêu?", 
                "40", "42", "43", "44", "42", 21);
            addSampleQuestion(db, catId5, "Số nào là số chẵn?", 
                "15", "23", "28", "31", "28", 22);
            addSampleQuestion(db, catId5, "Diện tích hình vuông có cạnh 5cm là bao nhiêu?", 
                "20cm²", "25cm²", "30cm²", "35cm²", "25cm²", 23);
            addSampleQuestion(db, catId5, "Số nào chia hết cho 3?", 
                "14", "16", "18", "20", "18", 24);
            addSampleQuestion(db, catId5, "Kết quả của 8 x 7 là bao nhiêu?", 
                "54", "56", "58", "60", "56", 25);
            addSampleQuestion(db, catId5, "Số Pi có giá trị gần bằng bao nhiêu?", 
                "3.14", "3.15", "3.16", "3.17", "3.14", 26);
            addSampleQuestion(db, catId5, "Căn bậc hai của 144 là bao nhiêu?", 
                "12", "14", "16", "18", "12", 27);
            addSampleQuestion(db, catId5, "Số nào là số nguyên tố?", 
                "15", "21", "17", "25", "17", 28);
            addSampleQuestion(db, catId5, "Tổng các góc trong tam giác là bao nhiêu độ?", 
                "180", "360", "90", "270", "180", 29);
            addSampleQuestion(db, catId5, "Kết quả của 15% của 200 là bao nhiêu?", 
                "30", "35", "40", "45", "30", 30);
            addSampleQuestion(db, catId5, "Chu vi hình tròn có bán kính 5cm là bao nhiêu?", 
                "31.4cm", "15.7cm", "10cm", "25cm", "31.4cm", 31);
            addSampleQuestion(db, catId5, "Số nào là số chính phương?", 
                "15", "24", "36", "48", "36", 32);
            addSampleQuestion(db, catId5, "Kết quả của 2³ là bao nhiêu?", 
                "6", "8", "9", "12", "8", 33);
            addSampleQuestion(db, catId5, "Phân số 3/4 bằng bao nhiêu phần trăm?", 
                "75%", "80%", "70%", "85%", "75%", 34);
            addSampleQuestion(db, catId5, "Một hình chữ nhật có chiều dài 8cm, chiều rộng 6cm, diện tích là bao nhiêu?", 
                "48cm²", "28cm²", "14cm²", "42cm²", "48cm²", 35);
            
            // Tạo thêm 5 user mẫu với role là "user"
            createSampleUsers(db);
            
            System.out.println("Sample data created successfully");
        } catch (Exception e) {
            System.out.println("Error creating sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createSampleUsers(SQLiteDatabase db) {
        try {
            System.out.println("Creating sample users...");
            
            ContentValues admin1 = new ContentValues();
            admin1.put(COLUMN_NAME, "My Admin");
            admin1.put(COLUMN_EMAIL, "ad1@gm.com");
            admin1.put(COLUMN_PASSWORD, "123456");
            admin1.put(COLUMN_ROLE, "admin");
            admin1.put(COLUMN_COINS, 0);
            long adminId1 = db.insert(TABLE_USERS, null, admin1);
            System.out.println("Sample user 1 created with ID: " + adminId1);
            // User 1
            ContentValues user1 = new ContentValues();
            user1.put(COLUMN_NAME, "Nguyễn Văn An");
            user1.put(COLUMN_EMAIL, "cus1@gm.com");
            user1.put(COLUMN_PASSWORD, "123456");
            user1.put(COLUMN_ROLE, "user");
            user1.put(COLUMN_COINS, 150);
            user1.put(COLUMN_AVATAR_IMAGE, "avatar_male_1"); // Avatar nam màu xanh
            long userId1 = db.insert(TABLE_USERS, null, user1);
            System.out.println("Sample user 1 created with ID: " + userId1);

            // User 2
            ContentValues user2 = new ContentValues();
            user2.put(COLUMN_NAME, "Trần Thị Bình");
            user2.put(COLUMN_EMAIL, "cus2@gm.com");
            user2.put(COLUMN_PASSWORD, "123456");
            user2.put(COLUMN_ROLE, "user");
            user2.put(COLUMN_COINS, 200);
            user2.put(COLUMN_AVATAR_IMAGE, "avatar_female_1"); // Avatar nữ màu hồng
            long userId2 = db.insert(TABLE_USERS, null, user2);
            System.out.println("Sample user 2 created with ID: " + userId2);

            // User 3
            ContentValues user3 = new ContentValues();
            user3.put(COLUMN_NAME, "Lê Văn Cường");
            user3.put(COLUMN_EMAIL, "cus3@gm.com");
            user3.put(COLUMN_PASSWORD, "123456");
            user3.put(COLUMN_ROLE, "user");
            user3.put(COLUMN_COINS, 300);
            user3.put(COLUMN_AVATAR_IMAGE, "avatar_male_2"); // Avatar nam màu xanh lá
            long userId3 = db.insert(TABLE_USERS, null, user3);
            System.out.println("Sample user 3 created with ID: " + userId3);

            // User 4
            ContentValues user4 = new ContentValues();
            user4.put(COLUMN_NAME, "Phạm Thị Dung");
            user4.put(COLUMN_EMAIL, "cus4@gm.com");
            user4.put(COLUMN_PASSWORD, "123456");
            user4.put(COLUMN_ROLE, "user");
            user4.put(COLUMN_COINS, 180);
            user4.put(COLUMN_AVATAR_IMAGE, "avatar_female_2"); // Avatar nữ màu tím
            long userId4 = db.insert(TABLE_USERS, null, user4);
            System.out.println("Sample user 4 created with ID: " + userId4);

            // User 5
            ContentValues user5 = new ContentValues();
            user5.put(COLUMN_NAME, "Võ Văn Em");
            user5.put(COLUMN_EMAIL, "cus5@gm.com");
            user5.put(COLUMN_PASSWORD, "123456");
            user5.put(COLUMN_ROLE, "user");
            user5.put(COLUMN_COINS, 250);
            user5.put(COLUMN_AVATAR_IMAGE, "avatar_male_3"); // Avatar nam màu đỏ cam
            long userId5 = db.insert(TABLE_USERS, null, user5);
            System.out.println("Sample user 5 created with ID: " + userId5);

            System.out.println("Sample users created successfully");
        } catch (Exception e) {
            System.out.println("Error creating sample users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addSampleQuestion(SQLiteDatabase db, long categoryId, String question, 
                                  String option1, String option2, String option3, String option4, 
                                  String answer, int index) {
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY_ID, categoryId);
            values.put(COLUMN_QUESTION_TEXT, question);
            values.put(COLUMN_OPTION1, option1);
            values.put(COLUMN_OPTION2, option2);
            values.put(COLUMN_OPTION3, option3);
            values.put(COLUMN_OPTION4, option4);
            values.put(COLUMN_ANSWER, answer);
            values.put(COLUMN_QUESTION_INDEX, index);
            long result = db.insert(TABLE_QUESTIONS, null, values);
            if (result == -1) {
                System.out.println("Failed to insert question: " + question);
            }
        } catch (Exception e) {
            System.out.println("Error adding sample question: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức xác thực user
    public User authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD, 
                           COLUMN_ROLE, COLUMN_COINS, COLUMN_REFER_CODE, COLUMN_PROFILE, COLUMN_PHONE, COLUMN_AVATAR_IMAGE};
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        
        if (cursor.moveToFirst()) {
            User user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));
            user.setCoins(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COINS)));
            user.setReferCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REFER_CODE)));
            user.setProfile(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            user.setAvatarImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_IMAGE)));
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    // Phương thức đăng ký user mới
    public long registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_ROLE, "user");
        values.put(COLUMN_COINS, user.getCoins());
        values.put(COLUMN_REFER_CODE, user.getReferCode());
        values.put(COLUMN_PROFILE, user.getProfile());
        values.put(COLUMN_PHONE, user.getPhone());
        values.put(COLUMN_AVATAR_IMAGE, user.getAvatarImage());
        return db.insert(TABLE_USERS, null, values);
    }

    // Phương thức lấy tất cả categories
    public List<CategoryModel> getAllCategories() {
        List<CategoryModel> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_CATEGORY_ID, COLUMN_CATEGORY_NAME, COLUMN_CATEGORY_IMAGE};
        
        Cursor cursor = db.query(TABLE_CATEGORIES, columns, null, null, null, null, null);
        
        while (cursor.moveToNext()) {
            CategoryModel category = new CategoryModel();
            category.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
            category.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)));
            category.setCategoryImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_IMAGE)));
            categories.add(category);
        }
        cursor.close();
        return categories;
    }

    // Phương thức lấy questions theo category
    public List<Question> getQuestionsByCategory(int categoryId, int limit) {
        List<Question> questions = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] columns = {COLUMN_QUESTION_ID, COLUMN_CATEGORY_ID, COLUMN_QUESTION_TEXT, 
                               COLUMN_OPTION1, COLUMN_OPTION2, COLUMN_OPTION3, COLUMN_OPTION4, COLUMN_ANSWER};
            String selection = COLUMN_CATEGORY_ID + " = ?";
            String[] selectionArgs = {String.valueOf(categoryId)};
            String orderBy = "RANDOM()";
            String limitClause = limit > 0 ? String.valueOf(limit) : null;
            
            System.out.println("DatabaseHelper: Querying questions for category " + categoryId);
            Cursor cursor = db.query(TABLE_QUESTIONS, columns, selection, selectionArgs, null, null, orderBy, limitClause);
            
            System.out.println("DatabaseHelper: Found " + cursor.getCount() + " questions");
            
            while (cursor.moveToNext()) {
                Question question = new Question();
                question.setQuestionId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID)));
                question.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT)));
                question.setOption1(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION1)));
                question.setOption2(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION2)));
                question.setOption3(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION3)));
                question.setOption4(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION4)));
                question.setAnswer(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER)));
                questions.add(question);
                System.out.println("DatabaseHelper: Added question: " + question.getQuestion());
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("DatabaseHelper: Error getting questions: " + e.getMessage());
            e.printStackTrace();
        }
        return questions;
    }

    // Phương thức cập nhật coins của user
    public void updateUserCoins(int userId, long coins) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COINS, coins);
        String whereClause = COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};
        db.update(TABLE_USERS, values, whereClause, whereArgs);
    }

    // Phương thức lấy user theo ID
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD, 
                           COLUMN_ROLE, COLUMN_COINS, COLUMN_REFER_CODE, COLUMN_PROFILE, COLUMN_PHONE, COLUMN_AVATAR_IMAGE};
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        
        if (cursor.moveToFirst()) {
            User user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));
            user.setCoins(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COINS)));
            user.setReferCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REFER_CODE)));
            user.setProfile(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            user.setAvatarImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_IMAGE)));
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    // Phương thức lấy tất cả users cho leaderboard
    public List<User> getAllUsersForLeaderboard() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD, 
                           COLUMN_ROLE, COLUMN_COINS, COLUMN_REFER_CODE, COLUMN_PROFILE, COLUMN_PHONE, COLUMN_AVATAR_IMAGE};
        String orderBy = COLUMN_COINS + " DESC";
        
        Cursor cursor = db.query(TABLE_USERS, columns, null, null, null, null, orderBy);
        
        while (cursor.moveToNext()) {
            User user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));
            user.setCoins(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COINS)));
            user.setReferCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REFER_CODE)));
            user.setProfile(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            user.setAvatarImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_IMAGE)));
            users.add(user);
        }
        cursor.close();
        return users;
    }

    // Phương thức tạo withdraw request
    public long createWithdrawRequest(WithdrawRequest request) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, request.getUserId());
        values.put(COLUMN_EMAIL_ADDRESS, request.getEmailAddress());
        values.put(COLUMN_REQUESTED_BY, request.getRequestedBy());
        return db.insert(TABLE_WITHDRAWS, null, values);
    }

    // Phương thức cập nhật thông tin user
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_ROLE, user.getRole());
        values.put(COLUMN_COINS, user.getCoins());
        values.put(COLUMN_REFER_CODE, user.getReferCode());
        values.put(COLUMN_PROFILE, user.getProfile());
        values.put(COLUMN_PHONE, user.getPhone());
        values.put(COLUMN_AVATAR_IMAGE, user.getAvatarImage());
        
        String whereClause = COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(user.getUserId())};
        db.update(TABLE_USERS, values, whereClause, whereArgs);
    }

    // Phương thức kiểm tra email đã tồn tại chưa
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Phương thức lấy user theo email
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD, 
                           COLUMN_ROLE, COLUMN_COINS, COLUMN_REFER_CODE, COLUMN_PROFILE, COLUMN_PHONE, COLUMN_AVATAR_IMAGE};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        
        if (cursor.moveToFirst()) {
            User user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));
            user.setCoins(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COINS)));
            user.setReferCode(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REFER_CODE)));
            user.setProfile(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILE)));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)));
            user.setAvatarImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_IMAGE)));
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    // Phương thức thêm category mới (cho admin)
    public long addCategory(CategoryModel category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, category.getCategoryName());
        values.put(COLUMN_CATEGORY_IMAGE, category.getCategoryImage());
        return db.insert(TABLE_CATEGORIES, null, values);
    }

    // Phương thức thêm question mới (cho admin)
    public long addQuestion(Question question, int categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_ID, categoryId);
        values.put(COLUMN_QUESTION_TEXT, question.getQuestion());
        values.put(COLUMN_OPTION1, question.getOption1());
        values.put(COLUMN_OPTION2, question.getOption2());
        values.put(COLUMN_OPTION3, question.getOption3());
        values.put(COLUMN_OPTION4, question.getOption4());
        values.put(COLUMN_ANSWER, question.getAnswer());
        values.put(COLUMN_QUESTION_INDEX, question.getQuestionId());
        return db.insert(TABLE_QUESTIONS, null, values);
    }

    // Phương thức cập nhật question (cho admin)
    public boolean updateQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_ID, question.getCategoryId());
        values.put(COLUMN_QUESTION_TEXT, question.getQuestion());
        values.put(COLUMN_OPTION1, question.getOption1());
        values.put(COLUMN_OPTION2, question.getOption2());
        values.put(COLUMN_OPTION3, question.getOption3());
        values.put(COLUMN_OPTION4, question.getOption4());
        values.put(COLUMN_ANSWER, question.getAnswer());
        
        String whereClause = COLUMN_QUESTION_ID + " = ?";
        String[] whereArgs = {String.valueOf(question.getQuestionId())};
        int result = db.update(TABLE_QUESTIONS, values, whereClause, whereArgs);
        return result > 0;
    }

    // Phương thức xóa question (cho admin)
    public boolean deleteQuestion(int questionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_QUESTION_ID + " = ?";
        String[] whereArgs = {String.valueOf(questionId)};
        int result = db.delete(TABLE_QUESTIONS, whereClause, whereArgs);
        return result > 0;
    }

    // Phương thức lấy tất cả questions (cho admin)
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_QUESTION_ID, COLUMN_CATEGORY_ID, COLUMN_QUESTION_TEXT, 
                           COLUMN_OPTION1, COLUMN_OPTION2, COLUMN_OPTION3, COLUMN_OPTION4, COLUMN_ANSWER};
        
        Cursor cursor = db.query(TABLE_QUESTIONS, columns, null, null, null, null, null);
        
        while (cursor.moveToNext()) {
            Question question = new Question();
            question.setQuestionId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID)));
            question.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
            question.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT)));
            question.setOption1(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION1)));
            question.setOption2(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION2)));
            question.setOption3(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION3)));
            question.setOption4(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION4)));
            question.setAnswer(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER)));
            questions.add(question);
        }
        cursor.close();
        return questions;
    }

    // Phương thức lấy question theo ID (cho EditQuestionActivity)
    public Question getQuestionById(int questionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_QUESTION_ID, COLUMN_CATEGORY_ID, COLUMN_QUESTION_TEXT, 
                           COLUMN_OPTION1, COLUMN_OPTION2, COLUMN_OPTION3, COLUMN_OPTION4, COLUMN_ANSWER};
        String selection = COLUMN_QUESTION_ID + " = ?";
        String[] selectionArgs = {String.valueOf(questionId)};
        
        Cursor cursor = db.query(TABLE_QUESTIONS, columns, selection, selectionArgs, null, null, null);
        
        if (cursor.moveToFirst()) {
            Question question = new Question();
            question.setQuestionId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID)));
            question.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
            question.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT)));
            question.setOption1(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION1)));
            question.setOption2(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION2)));
            question.setOption3(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION3)));
            question.setOption4(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OPTION4)));
            question.setAnswer(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER)));
            cursor.close();
            return question;
        }
        cursor.close();
        return null;
    }

    // Phương thức lấy tất cả withdraw requests (cho admin)
    public List<WithdrawRequest> getAllWithdrawRequests() {
        List<WithdrawRequest> requests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_WITHDRAW_ID, COLUMN_USER_ID, COLUMN_EMAIL_ADDRESS, 
                           COLUMN_REQUESTED_BY, COLUMN_CREATED_AT};
        String orderBy = COLUMN_CREATED_AT + " DESC";
        
        Cursor cursor = db.query(TABLE_WITHDRAWS, columns, null, null, null, null, orderBy);
        
        while (cursor.moveToNext()) {
            WithdrawRequest request = new WithdrawRequest();
            request.setWithdrawId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WITHDRAW_ID)));
            request.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            request.setEmailAddress(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_ADDRESS)));
            request.setRequestedBy(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUESTED_BY)));
            
            // Chuyển đổi String thành Date
            String dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT));
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(dateString);
                request.setCreatedAt(date);
            } catch (Exception e) {
                // Nếu không parse được, sử dụng thời gian hiện tại
                request.setCreatedAt(new Date());
            }
            
            requests.add(request);
        }
        cursor.close();
        return requests;
    }

    // Phương thức xóa withdraw request (cho admin)
    public boolean deleteWithdrawRequest(int withdrawId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = COLUMN_WITHDRAW_ID + " = ?";
        String[] whereArgs = {String.valueOf(withdrawId)};
        int result = db.delete(TABLE_WITHDRAWS, whereClause, whereArgs);
        return result > 0;
    }

    // Phương thức để force tạo lại database (cho debug)
    public void recreateDatabase() {
        try {
            System.out.println("Recreating database...");
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_RESULTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WITHDRAWS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
            System.out.println("Database recreated successfully");
        } catch (Exception e) {
            System.out.println("Error recreating database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức cập nhật mật khẩu user
    public boolean updateUserPassword(int userId, String currentPassword, String newPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Kiểm tra mật khẩu hiện tại
        String[] columns = {COLUMN_PASSWORD};
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        
        if (cursor.moveToFirst()) {
            String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            cursor.close();
            
            if (storedPassword.equals(currentPassword)) {
                // Cập nhật mật khẩu mới
                SQLiteDatabase writeDb = this.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(COLUMN_PASSWORD, newPassword);
                
                String whereClause = COLUMN_USER_ID + " = ?";
                String[] whereArgs = {String.valueOf(userId)};
                int result = writeDb.update(TABLE_USERS, values, whereClause, whereArgs);
                return result > 0;
            }
        } else {
            cursor.close();
        }
        return false;
    }

    // Phương thức debug để kiểm tra tất cả quiz results
    public void debugAllQuizResults() {
        try {
            System.out.println("=== DEBUG: All Quiz Results ===");
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_QUIZ_RESULTS;
            Cursor cursor = db.rawQuery(query, null);
            
            System.out.println("Total quiz results in database: " + cursor.getCount());
            
            while (cursor.moveToNext()) {
                int resultId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESULT_ID));
                int userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
                String completedAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED_AT));
                
                System.out.println("Result ID: " + resultId + ", User ID: " + userId + 
                                  ", Category ID: " + categoryId + ", Score: " + score + 
                                  ", Completed: " + completedAt);
            }
            cursor.close();
            System.out.println("=== END DEBUG ===");
        } catch (Exception e) {
            System.out.println("Debug error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức cập nhật avatar user
    public boolean updateUserAvatar(int userId, String avatarImagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AVATAR_IMAGE, avatarImagePath);
        
        String whereClause = COLUMN_USER_ID + " = ?";
        String[] whereArgs = {String.valueOf(userId)};
        int result = db.update(TABLE_USERS, values, whereClause, whereArgs);
        return result > 0;
    }

    // Phương thức thêm kết quả quiz
    public long addQuizResult(int userId, int categoryId, int score, int totalQuestions, 
                             int correctAnswers, long completionTime) {
        try {
            System.out.println("DatabaseHelper: Adding quiz result - userId: " + userId + 
                              ", categoryId: " + categoryId + ", score: " + score + 
                              ", totalQuestions: " + totalQuestions + ", correctAnswers: " + correctAnswers + 
                              ", completionTime: " + completionTime);
                              
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_CATEGORY_ID, categoryId);
            values.put(COLUMN_SCORE, score);
            values.put(COLUMN_TOTAL_QUESTIONS, totalQuestions);
            values.put(COLUMN_CORRECT_ANSWERS, correctAnswers);
            values.put(COLUMN_COMPLETION_TIME, completionTime);
            
            long result = db.insert(TABLE_QUIZ_RESULTS, null, values);
            System.out.println("DatabaseHelper: Quiz result insert returned: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("DatabaseHelper: Error adding quiz result: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    // Phương thức lấy tất cả kết quả quiz của user
    public List<QuizResult> getQuizResultsByUser(int userId) {
        List<QuizResult> results = new ArrayList<>();
        try {
            System.out.println("DatabaseHelper: Getting quiz results for userId: " + userId);
            SQLiteDatabase db = this.getReadableDatabase();
            
            String query = "SELECT qr." + COLUMN_RESULT_ID + ", qr." + COLUMN_USER_ID + ", qr." + COLUMN_CATEGORY_ID + 
                          ", qr." + COLUMN_SCORE + ", qr." + COLUMN_TOTAL_QUESTIONS + ", qr." + COLUMN_CORRECT_ANSWERS + 
                          ", qr." + COLUMN_COMPLETION_TIME + ", qr." + COLUMN_COMPLETED_AT + 
                          ", c." + COLUMN_CATEGORY_NAME + ", c." + COLUMN_CATEGORY_IMAGE + 
                          " FROM " + TABLE_QUIZ_RESULTS + " qr" +
                          " JOIN " + TABLE_CATEGORIES + " c ON qr." + COLUMN_CATEGORY_ID + " = c." + COLUMN_CATEGORY_ID +
                          " WHERE qr." + COLUMN_USER_ID + " = ?" +
                          " ORDER BY qr." + COLUMN_COMPLETED_AT + " DESC";
            
            System.out.println("DatabaseHelper: Executing query: " + query);
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
            
            System.out.println("DatabaseHelper: Found " + cursor.getCount() + " quiz results");
            
            while (cursor.moveToNext()) {
                QuizResult result = new QuizResult();
                result.setResultId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESULT_ID)));
                result.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
                result.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                result.setScore(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)));
                result.setTotalQuestions(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_QUESTIONS)));
                result.setCorrectAnswers(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CORRECT_ANSWERS)));
                result.setCompletionTime(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COMPLETION_TIME)));
                result.setCompletedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED_AT)));
                result.setCategoryName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_NAME)));
                result.setCategoryImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_IMAGE)));
                results.add(result);
                System.out.println("DatabaseHelper: Added result - Score: " + result.getScore() + 
                                  ", Category: " + result.getCategoryName());
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println("DatabaseHelper: Error getting quiz results: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    // Phương thức lấy thống kê quiz của user
    public QuizStats getQuizStats(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        QuizStats stats = new QuizStats();
        
        String query = "SELECT COUNT(*) as totalQuizzes, " +
                      "AVG(" + COLUMN_SCORE + ") as avgScore, " +
                      "MAX(" + COLUMN_SCORE + ") as bestScore, " +
                      "SUM(" + COLUMN_CORRECT_ANSWERS + ") as totalCorrect, " +
                      "SUM(" + COLUMN_TOTAL_QUESTIONS + ") as totalQuestions " +
                      "FROM " + TABLE_QUIZ_RESULTS + " WHERE " + COLUMN_USER_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        
        if (cursor.moveToFirst()) {
            stats.setTotalQuizzes(cursor.getInt(cursor.getColumnIndexOrThrow("totalQuizzes")));
            stats.setAverageScore(cursor.getFloat(cursor.getColumnIndexOrThrow("avgScore")));
            stats.setBestScore(cursor.getInt(cursor.getColumnIndexOrThrow("bestScore")));
            stats.setTotalCorrectAnswers(cursor.getInt(cursor.getColumnIndexOrThrow("totalCorrect")));
            stats.setTotalQuestions(cursor.getInt(cursor.getColumnIndexOrThrow("totalQuestions")));
        }
        cursor.close();
        return stats;
    }

    // ========================= FRIEND SYSTEM & MESSAGING METHODS =========================
    
    // Constants for CREATE TABLE statements (placed here to avoid disrupting existing code)
    private static final String CREATE_TABLE_FRIEND_REQUESTS = "CREATE TABLE " + TABLE_FRIEND_REQUESTS + " (" +
            COLUMN_REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SENDER_ID + " INTEGER NOT NULL, " +
            COLUMN_RECEIVER_ID + " INTEGER NOT NULL, " +
            COLUMN_STATUS + " TEXT DEFAULT 'pending', " +
            COLUMN_REQUEST_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            COLUMN_RESPONSE_DATE + " DATETIME, " +
            "FOREIGN KEY(" + COLUMN_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
            "FOREIGN KEY(" + COLUMN_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
            "UNIQUE(" + COLUMN_SENDER_ID + ", " + COLUMN_RECEIVER_ID + "))";

    private static final String CREATE_TABLE_FRIENDS = "CREATE TABLE " + TABLE_FRIENDS + " (" +
            COLUMN_FRIENDSHIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_USER1_ID + " INTEGER NOT NULL, " +
            COLUMN_USER2_ID + " INTEGER NOT NULL, " +
            COLUMN_FRIENDSHIP_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(" + COLUMN_USER1_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
            "FOREIGN KEY(" + COLUMN_USER2_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
            "UNIQUE(" + COLUMN_USER1_ID + ", " + COLUMN_USER2_ID + "))";

    private static final String CREATE_TABLE_MESSAGES = "CREATE TABLE " + TABLE_MESSAGES + " (" +
            COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SENDER_USER_ID + " INTEGER NOT NULL, " +
            COLUMN_RECEIVER_USER_ID + " INTEGER NOT NULL, " +
            COLUMN_MESSAGE_CONTENT + " TEXT NOT NULL, " +
            COLUMN_MESSAGE_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            COLUMN_IS_READ + " INTEGER DEFAULT 0, " +
            "FOREIGN KEY(" + COLUMN_SENDER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "), " +
            "FOREIGN KEY(" + COLUMN_RECEIVER_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

    // ========================= FRIEND SYSTEM METHODS =========================
    
    // Tìm kiếm user bằng email hoặc name
    public List<User> searchUsers(String searchQuery, int currentUserId) {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String[] columns = {COLUMN_USER_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_AVATAR_IMAGE, COLUMN_COINS};
        String selection = "(" + COLUMN_NAME + " LIKE ? OR " + COLUMN_EMAIL + " LIKE ?) AND " + 
                          COLUMN_USER_ID + " != ? AND " + COLUMN_ROLE + " = 'user'";
        String[] selectionArgs = {"%" + searchQuery + "%", "%" + searchQuery + "%", String.valueOf(currentUserId)};
        
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, COLUMN_NAME);
        
        while (cursor.moveToNext()) {
            User user = new User();
            user.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setAvatarImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AVATAR_IMAGE)));
            user.setCoins(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_COINS)));
            users.add(user);
        }
        cursor.close();
        return users;
    }
    
    // Gửi lời mời kết bạn
    public long sendFriendRequest(int senderId, int receiverId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Kiểm tra xem đã có lời mời nào chưa
        if (friendRequestExists(senderId, receiverId)) {
            return -1; // Đã tồn tại lời mời
        }
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER_ID, senderId);
        values.put(COLUMN_RECEIVER_ID, receiverId);
        values.put(COLUMN_STATUS, "pending");
        
        return db.insert(TABLE_FRIEND_REQUESTS, null, values);
    }
    
    // Kiểm tra xem lời mời kết bạn đã tồn tại chưa
    private boolean friendRequestExists(int senderId, int receiverId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = "(" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?) OR " +
                          "(" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?)";
        String[] selectionArgs = {String.valueOf(senderId), String.valueOf(receiverId), 
                                 String.valueOf(receiverId), String.valueOf(senderId)};
        
        Cursor cursor = db.query(TABLE_FRIEND_REQUESTS, null, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    
    // Kiểm tra trạng thái kết bạn giữa hai user
    public String getFriendshipStatus(int currentUserId, int targetUserId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Kiểm tra xem đã là bạn bè chưa
        String friendSelection = "(" + COLUMN_USER1_ID + " = ? AND " + COLUMN_USER2_ID + " = ?) OR " +
                               "(" + COLUMN_USER1_ID + " = ? AND " + COLUMN_USER2_ID + " = ?)";
        String[] friendArgs = {String.valueOf(currentUserId), String.valueOf(targetUserId),
                              String.valueOf(targetUserId), String.valueOf(currentUserId)};
        
        Cursor friendCursor = db.query(TABLE_FRIENDS, null, friendSelection, friendArgs, null, null, null);
        if (friendCursor.getCount() > 0) {
            friendCursor.close();
            return "FRIENDS"; // Đã là bạn bè
        }
        friendCursor.close();
        
        // Kiểm tra xem có lời mời kết bạn pending không
        String requestSelection = COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ? AND " + COLUMN_STATUS + " = 'pending'";
        String[] requestArgs = {String.valueOf(currentUserId), String.valueOf(targetUserId)};
        
        Cursor requestCursor = db.query(TABLE_FRIEND_REQUESTS, null, requestSelection, requestArgs, null, null, null);
        if (requestCursor.getCount() > 0) {
            requestCursor.close();
            return "REQUEST_SENT"; // Đã gửi lời mời
        }
        requestCursor.close();
        
        // Kiểm tra xem có nhận lời mời từ user này không
        String receivedSelection = COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ? AND " + COLUMN_STATUS + " = 'pending'";
        String[] receivedArgs = {String.valueOf(targetUserId), String.valueOf(currentUserId)};
        
        Cursor receivedCursor = db.query(TABLE_FRIEND_REQUESTS, null, receivedSelection, receivedArgs, null, null, null);
        if (receivedCursor.getCount() > 0) {
            receivedCursor.close();
            return "REQUEST_RECEIVED"; // Nhận được lời mời
        }
        receivedCursor.close();
        
        return "NONE"; // Không có quan hệ gì
    }
    
    // Lấy danh sách lời mời kết bạn nhận được
    public List<FriendRequest> getReceivedFriendRequests(int userId) {
        List<FriendRequest> requests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT fr." + COLUMN_REQUEST_ID + ", fr." + COLUMN_SENDER_ID + ", fr." + COLUMN_RECEIVER_ID + 
                      ", fr." + COLUMN_STATUS + ", fr." + COLUMN_REQUEST_DATE + 
                      ", u." + COLUMN_NAME + " as sender_name, u." + COLUMN_EMAIL + " as sender_email, u." + COLUMN_AVATAR_IMAGE + " as sender_avatar" +
                      " FROM " + TABLE_FRIEND_REQUESTS + " fr" +
                      " JOIN " + TABLE_USERS + " u ON fr." + COLUMN_SENDER_ID + " = u." + COLUMN_USER_ID +
                      " WHERE fr." + COLUMN_RECEIVER_ID + " = ? AND fr." + COLUMN_STATUS + " = 'pending'" +
                      " ORDER BY fr." + COLUMN_REQUEST_DATE + " DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        
        while (cursor.moveToNext()) {
            FriendRequest request = new FriendRequest();
            request.setRequestId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_ID)));
            request.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SENDER_ID)));
            request.setReceiverId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECEIVER_ID)));
            request.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            request.setSenderName(cursor.getString(cursor.getColumnIndexOrThrow("sender_name")));
            request.setSenderEmail(cursor.getString(cursor.getColumnIndexOrThrow("sender_email")));
            request.setSenderAvatar(cursor.getString(cursor.getColumnIndexOrThrow("sender_avatar")));
            
            // Parse request date
            String dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_DATE));
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                request.setRequestDate(sdf.parse(dateString));
            } catch (Exception e) {
                request.setRequestDate(new Date());
            }
            
            requests.add(request);
        }
        cursor.close();
        return requests;
    }
    
    // Chấp nhận lời mời kết bạn
    public boolean acceptFriendRequest(int requestId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Lấy thông tin request
        FriendRequest request = getFriendRequestById(requestId);
        if (request == null) return false;
        
        try {
            db.beginTransaction();
            
            // Cập nhật status của request
            ContentValues values = new ContentValues();
            values.put(COLUMN_STATUS, "accepted");
            values.put(COLUMN_RESPONSE_DATE, new Date().getTime());
            
            String whereClause = COLUMN_REQUEST_ID + " = ?";
            String[] whereArgs = {String.valueOf(requestId)};
            db.update(TABLE_FRIEND_REQUESTS, values, whereClause, whereArgs);
            
            // Thêm vào bảng friends
            ContentValues friendValues = new ContentValues();
            friendValues.put(COLUMN_USER1_ID, Math.min(request.getSenderId(), request.getReceiverId()));
            friendValues.put(COLUMN_USER2_ID, Math.max(request.getSenderId(), request.getReceiverId()));
            
            long result = db.insert(TABLE_FRIENDS, null, friendValues);
            
            if (result != -1) {
                db.setTransactionSuccessful();
                return true;
            }
            return false;
        } finally {
            db.endTransaction();
        }
    }
    
    // Từ chối lời mời kết bạn
    public boolean rejectFriendRequest(int requestId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, "rejected");
        values.put(COLUMN_RESPONSE_DATE, new Date().getTime());
        
        String whereClause = COLUMN_REQUEST_ID + " = ?";
        String[] whereArgs = {String.valueOf(requestId)};
        
        int result = db.update(TABLE_FRIEND_REQUESTS, values, whereClause, whereArgs);
        return result > 0;
    }
    
    // Từ chối lời mời kết bạn (alias)
    public boolean declineFriendRequest(int requestId) {
        return rejectFriendRequest(requestId);
    }
    
    // Lấy danh sách lời mời kết bạn đang chờ
    public List<FriendRequest> getPendingFriendRequests(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<FriendRequest> requests = new ArrayList<>();
        
        String query = "SELECT fr." + COLUMN_REQUEST_ID + ", fr." + COLUMN_SENDER_ID + ", fr." + COLUMN_RECEIVER_ID + 
                       ", fr." + COLUMN_STATUS + ", fr." + COLUMN_REQUEST_DATE + 
                       ", u." + COLUMN_NAME + " as sender_name, u." + COLUMN_EMAIL + " as sender_email" +
                       ", u." + COLUMN_AVATAR_IMAGE + " as sender_avatar" +
                       " FROM " + TABLE_FRIEND_REQUESTS + " fr" +
                       " INNER JOIN " + TABLE_USERS + " u ON fr." + COLUMN_SENDER_ID + " = u." + COLUMN_USER_ID +
                       " WHERE fr." + COLUMN_RECEIVER_ID + " = ? AND fr." + COLUMN_STATUS + " = 'pending'" +
                       " ORDER BY fr." + COLUMN_REQUEST_DATE + " DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    FriendRequest request = new FriendRequest();
                    request.setRequestId(cursor.getInt(cursor.getColumnIndex(COLUMN_REQUEST_ID)));
                    request.setSenderId(cursor.getInt(cursor.getColumnIndex(COLUMN_SENDER_ID)));
                    request.setReceiverId(cursor.getInt(cursor.getColumnIndex(COLUMN_RECEIVER_ID)));
                    request.setStatus(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS)));
                    
                    // Set request date
                    long dateMillis = cursor.getLong(cursor.getColumnIndex(COLUMN_REQUEST_DATE));
                    request.setRequestDate(new Date(dateMillis));
                    
                    // Set sender info
                    request.setSenderName(cursor.getString(cursor.getColumnIndex("sender_name")));
                    request.setSenderEmail(cursor.getString(cursor.getColumnIndex("sender_email")));
                    request.setSenderAvatar(cursor.getString(cursor.getColumnIndex("sender_avatar")));
                    
                    requests.add(request);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        
        return requests;
    }
    
    // Lấy friend request theo ID
    private FriendRequest getFriendRequestById(int requestId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_REQUEST_ID, COLUMN_SENDER_ID, COLUMN_RECEIVER_ID, COLUMN_STATUS};
        String selection = COLUMN_REQUEST_ID + " = ?";
        String[] selectionArgs = {String.valueOf(requestId)};
        
        Cursor cursor = db.query(TABLE_FRIEND_REQUESTS, columns, selection, selectionArgs, null, null, null);
        
        if (cursor.moveToFirst()) {
            FriendRequest request = new FriendRequest();
            request.setRequestId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_REQUEST_ID)));
            request.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SENDER_ID)));
            request.setReceiverId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECEIVER_ID)));
            request.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            cursor.close();
            return request;
        }
        cursor.close();
        return null;
    }
    
    // Lấy danh sách bạn bè
    public List<Friend> getFriends(int userId) {
        List<Friend> friends = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT f." + COLUMN_FRIENDSHIP_ID + ", f." + COLUMN_USER1_ID + ", f." + COLUMN_USER2_ID + 
                      ", f." + COLUMN_FRIENDSHIP_DATE + 
                      ", u." + COLUMN_NAME + " as friend_name, u." + COLUMN_EMAIL + " as friend_email" +
                      ", u." + COLUMN_AVATAR_IMAGE + " as friend_avatar, u." + COLUMN_COINS + " as friend_coins" +
                      " FROM " + TABLE_FRIENDS + " f" +
                      " JOIN " + TABLE_USERS + " u ON (CASE WHEN f." + COLUMN_USER1_ID + " = ? THEN f." + COLUMN_USER2_ID + " ELSE f." + COLUMN_USER1_ID + " END) = u." + COLUMN_USER_ID +
                      " WHERE f." + COLUMN_USER1_ID + " = ? OR f." + COLUMN_USER2_ID + " = ?" +
                      " ORDER BY u." + COLUMN_NAME;
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(userId), String.valueOf(userId)});
        
        while (cursor.moveToNext()) {
            Friend friend = new Friend();
            friend.setFriendshipId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FRIENDSHIP_ID)));
            friend.setUser1Id(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER1_ID)));
            friend.setUser2Id(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER2_ID)));
            friend.setFriendName(cursor.getString(cursor.getColumnIndexOrThrow("friend_name")));
            friend.setFriendEmail(cursor.getString(cursor.getColumnIndexOrThrow("friend_email")));
            friend.setFriendAvatar(cursor.getString(cursor.getColumnIndexOrThrow("friend_avatar")));
            friend.setFriendCoins(cursor.getLong(cursor.getColumnIndexOrThrow("friend_coins")));
            
            // Parse friendship date
            String dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FRIENDSHIP_DATE));
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                friend.setFriendshipDate(sdf.parse(dateString));
            } catch (Exception e) {
                friend.setFriendshipDate(new Date());
            }
            
            friends.add(friend);
        }
        cursor.close();
        return friends;
    }
    
    // ========================= MESSAGING METHODS =========================
    
    // Gửi tin nhắn
    public long sendMessage(int senderId, int receiverId, String messageContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER_USER_ID, senderId);
        values.put(COLUMN_RECEIVER_USER_ID, receiverId);
        values.put(COLUMN_MESSAGE_CONTENT, messageContent);
        values.put(COLUMN_MESSAGE_DATE, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        values.put(COLUMN_IS_READ, 0);
        
        return db.insert(TABLE_MESSAGES, null, values);
    }
    
    // Lấy tin nhắn giữa 2 user
    public List<Message> getMessagesBetweenUsers(int user1Id, int user2Id) {
        List<Message> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT m." + COLUMN_MESSAGE_ID + ", m." + COLUMN_SENDER_USER_ID + ", m." + COLUMN_RECEIVER_USER_ID + 
                      ", m." + COLUMN_MESSAGE_CONTENT + ", m." + COLUMN_MESSAGE_DATE + ", m." + COLUMN_IS_READ +
                      ", s." + COLUMN_NAME + " as sender_name, s." + COLUMN_AVATAR_IMAGE + " as sender_avatar" +
                      ", r." + COLUMN_NAME + " as receiver_name, r." + COLUMN_AVATAR_IMAGE + " as receiver_avatar" +
                      " FROM " + TABLE_MESSAGES + " m" +
                      " JOIN " + TABLE_USERS + " s ON m." + COLUMN_SENDER_USER_ID + " = s." + COLUMN_USER_ID +
                      " JOIN " + TABLE_USERS + " r ON m." + COLUMN_RECEIVER_USER_ID + " = r." + COLUMN_USER_ID +
                      " WHERE (m." + COLUMN_SENDER_USER_ID + " = ? AND m." + COLUMN_RECEIVER_USER_ID + " = ?) OR " +
                      "(m." + COLUMN_SENDER_USER_ID + " = ? AND m." + COLUMN_RECEIVER_USER_ID + " = ?)" +
                      " ORDER BY m." + COLUMN_MESSAGE_DATE + " ASC";
        
        Cursor cursor = db.rawQuery(query, new String[]{
            String.valueOf(user1Id), String.valueOf(user2Id),
            String.valueOf(user2Id), String.valueOf(user1Id)
        });
        
        while (cursor.moveToNext()) {
            Message message = new Message();
            message.setMessageId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_ID)));
            message.setSenderUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SENDER_USER_ID)));
            message.setReceiverUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECEIVER_USER_ID)));
            message.setMessageContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_CONTENT)));
            message.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_READ)) == 1);
            message.setSenderName(cursor.getString(cursor.getColumnIndexOrThrow("sender_name")));
            message.setSenderAvatar(cursor.getString(cursor.getColumnIndexOrThrow("sender_avatar")));
            message.setReceiverName(cursor.getString(cursor.getColumnIndexOrThrow("receiver_name")));
            message.setReceiverAvatar(cursor.getString(cursor.getColumnIndexOrThrow("receiver_avatar")));
            
            // Parse message date
            String dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE_DATE));
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                message.setMessageDate(sdf.parse(dateString));
            } catch (Exception e) {
                message.setMessageDate(new Date());
            }
            
            messages.add(message);
        }
        cursor.close();
        return messages;
    }
    
    // Đánh dấu tin nhắn đã đọc
    public void markMessageAsRead(int messageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);
        
        String whereClause = COLUMN_MESSAGE_ID + " = ?";
        String[] whereArgs = {String.valueOf(messageId)};
        
        db.update(TABLE_MESSAGES, values, whereClause, whereArgs);
    }
    
    // Đánh dấu tất cả tin nhắn từ user cụ thể đã đọc
    public void markAllMessagesAsRead(int senderId, int receiverId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);
        
        String whereClause = COLUMN_SENDER_USER_ID + " = ? AND " + COLUMN_RECEIVER_USER_ID + " = ? AND " + COLUMN_IS_READ + " = 0";
        String[] whereArgs = {String.valueOf(senderId), String.valueOf(receiverId)};
        
        db.update(TABLE_MESSAGES, values, whereClause, whereArgs);
    }
    
    // Admin statistics methods
    public int getTotalUsersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    
    public int getTotalQuestionsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUESTIONS, null, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    
    public int getTotalCategoriesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    
    // Lấy số lượng bạn bè của user
    public int getFriendsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER1_ID + " = ? OR " + COLUMN_USER2_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(userId)};
        
        Cursor cursor = db.query(TABLE_FRIENDS, null, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    
    // Lấy số tin nhắn chưa đọc
    public int getUnreadMessageCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_RECEIVER_USER_ID + " = ? AND " + COLUMN_IS_READ + " = 0";
        String[] selectionArgs = {String.valueOf(userId)};
        
        Cursor cursor = db.query(TABLE_MESSAGES, null, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}