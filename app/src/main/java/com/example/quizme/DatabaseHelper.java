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
    private static final int DATABASE_VERSION = 2;

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

    // Tạo bảng Users
    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
            COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT NOT NULL, " +
            COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, " +
            COLUMN_PASSWORD + " TEXT NOT NULL, " +
            COLUMN_ROLE + " TEXT DEFAULT 'user', " +
            COLUMN_COINS + " INTEGER DEFAULT 25, " +
            COLUMN_REFER_CODE + " TEXT, " +
            COLUMN_PROFILE + " TEXT)";

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
            
            System.out.println("Sample data created successfully");
        } catch (Exception e) {
            System.out.println("Error creating sample data: " + e.getMessage());
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
                           COLUMN_ROLE, COLUMN_COINS, COLUMN_REFER_CODE, COLUMN_PROFILE};
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
                           COLUMN_ROLE, COLUMN_COINS, COLUMN_REFER_CODE, COLUMN_PROFILE};
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
                           COLUMN_ROLE, COLUMN_COINS, COLUMN_REFER_CODE, COLUMN_PROFILE};
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
                           COLUMN_ROLE, COLUMN_COINS, COLUMN_REFER_CODE, COLUMN_PROFILE};
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
} 