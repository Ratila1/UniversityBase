package com.ratila.universitybase1;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "university_db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_UNIVERSITY_TABLE = "CREATE TABLE universities (id INTEGER PRIMARY KEY, name TEXT, city_id INTEGER, description TEXT, contacts TEXT, website TEXT, image_url TEXT)";
        String CREATE_CITY_TABLE = "CREATE TABLE cities (id INTEGER PRIMARY KEY, name TEXT)";
        String CREATE_SPECIALTY_TABLE = "CREATE TABLE specialties (id INTEGER PRIMARY KEY, name TEXT)";
        String CREATE_UNIV_SPEC_TABLE = "CREATE TABLE university_specialties (id INTEGER PRIMARY KEY, university_id INTEGER, specialty_id INTEGER)";
        db.execSQL(CREATE_UNIVERSITY_TABLE);
        db.execSQL(CREATE_CITY_TABLE);
        db.execSQL(CREATE_SPECIALTY_TABLE);
        db.execSQL(CREATE_UNIV_SPEC_TABLE);
        populateDatabase(db);
    }

    public List<Specialty> getSpecialtiesByUniversityId(int universityId) {
        List<Specialty> specialties = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT s.id, s.name FROM specialties s JOIN university_specialties us ON s.id = us.specialty_id WHERE us.university_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(universityId)});

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    Specialty specialty = new Specialty();
                    int idIndex = cursor.getColumnIndex("id");
                    int nameIndex = cursor.getColumnIndex("name");

                    if (idIndex != -1 && nameIndex != -1) {
                        specialty.setId(cursor.getInt(idIndex));
                        specialty.setName(cursor.getString(nameIndex));
                        specialties.add(specialty);
                    } else {
                        Log.e("DatabaseHelper", "Column index not found in cursor");
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return specialties;
    }

    public List<University> getAllUniversities() {
        List<University> universities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("universities", null, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    University university = new University();
                    int idIndex = cursor.getColumnIndex("id");
                    int nameIndex = cursor.getColumnIndex("name");
                    int cityIdIndex = cursor.getColumnIndex("city_id");
                    int descriptionIndex = cursor.getColumnIndex("description");
                    int contactsIndex = cursor.getColumnIndex("contacts");
                    int websiteIndex = cursor.getColumnIndex("website");
                    int imageUrlIndex = cursor.getColumnIndex("image_url");

                    if (idIndex != -1 && nameIndex != -1 && cityIdIndex != -1 && descriptionIndex != -1 && contactsIndex != -1 && websiteIndex != -1 && imageUrlIndex != -1) {
                        university.setId(cursor.getInt(idIndex));
                        university.setName(cursor.getString(nameIndex));
                        university.setCityId(cursor.getInt(cityIdIndex));
                        university.setDescription(cursor.getString(descriptionIndex));
                        university.setContacts(cursor.getString(contactsIndex));
                        university.setWebsite(cursor.getString(websiteIndex));
                        university.setImageUrl(cursor.getString(imageUrlIndex));
                        universities.add(university);
                    } else {
                        Log.e("DatabaseHelper", "Column index not found in cursor");
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return universities;
    }

    public String getCityNameById(int cityId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cities", new String[]{"name"}, "id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        String cityName = "";
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex("name");
                if (nameIndex != -1) {
                    cityName = cursor.getString(nameIndex);
                } else {
                    Log.e("DatabaseHelper", "Column 'name' not found in cursor");
                }
            }
            cursor.close();
        }
        return cityName;
    }

    public int getCityIdByName(String cityName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cities", new String[]{"id"}, "name = ?", new String[]{cityName}, null, null, null);
        int cityId = -1;
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("id");
                if (idIndex != -1) {
                    cityId = cursor.getInt(idIndex);
                } else {
                    Log.e("DatabaseHelper", "Column 'id' not found in cursor");
                }
            }
            cursor.close();
        }
        return cityId;
    }

    public void deleteUniversitySpecialties(long universityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("university_specialties", "university_id = ?", new String[]{String.valueOf(universityId)});
    }

    public int getSpecialtyIdByName(String specialtyName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("specialties", new String[]{"id"}, "name = ?", new String[]{specialtyName}, null, null, null);
        int specialtyId = -1;
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("id");
                if (idIndex != -1) {
                    specialtyId = cursor.getInt(idIndex);
                } else {
                    Log.e("DatabaseHelper", "Column 'id' not found in cursor");
                }
            }
            cursor.close();
        }
        return specialtyId;
    }
    public void addUniversitySpecialty(long universityId, int specialtyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("university_id", universityId);
        values.put("specialty_id", specialtyId);
        db.insert("university_specialties", null, values);
    }
    public long addUniversity(University university) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", university.getName());
        values.put("city_id", university.getCityId());
        values.put("description", university.getDescription());
        values.put("contacts", university.getContacts());
        values.put("website", university.getWebsite());
        values.put("image_url", university.getImageUrl());
        return db.insert("universities", null, values);
    }

    public int addCity(String cityName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", cityName);
        long newRowId = db.insert("cities", null, values);
        return (int) newRowId;
    }

    public void updateUniversity(University university) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", university.getName());
        values.put("city_id", university.getCityId());
        values.put("description", university.getDescription());
        values.put("contacts", university.getContacts());
        values.put("website", university.getWebsite());
        values.put("image_url", university.getImageUrl());
        db.update("universities", values, "id = ?", new String[]{String.valueOf(university.getId())});
    }

    public void deleteUniversity(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("universities", "id = ?", new String[]{String.valueOf(id)});
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Обновление базы данных
        db.execSQL("DROP TABLE IF EXISTS universities");
        db.execSQL("DROP TABLE IF EXISTS cities");
        db.execSQL("DROP TABLE IF EXISTS specialties");
        db.execSQL("DROP TABLE IF EXISTS university_specialties");
        onCreate(db);
    }   

    private void populateDatabase(SQLiteDatabase db) {
        // Заполнение таблицы городов (без изменений)
        String[] cities = {"Cambridge", "Stanford", "New York", "Chicago", "Paris", "Berlin", "Tokyo", "Sydney", "Moscow", "Toronto"};
        for (int i = 0; i < cities.length; i++) {
            ContentValues values = new ContentValues();
            values.put("name", cities[i]);
            db.insert("cities", null, values);
        }

        // Заполнение таблицы университетов с добавлением image_url
        Object[][] universities = {
                {"Hsadasdadad University", 1, "Один из старейших и престижнейших университетов мира.", "contact@harvard.edu", "https://www.harvard.edu", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTKeGOjWNppV1aB8afhKory_JGSyCxEAebHgw&s"},
                {"Stanford University", 2, "Известен своими достижениями в области технологий и бизнеса.", "contact@stanford.edu", "https://www.stanford.edu", "https://olmsted.org/wp-content/uploads/2023/06/Main-Quad-from-Palm-Dive-by-Linda-Cicero.png"},
                {"Columbia University", 3, "Ведущий исследовательский университет в США.", "contact@columbia.edu", "https://www.columbia.edu", "https://cdn.kslnewsradio.com/kslnewsradio/wp-content/uploads/2024/04/cnn-L19jb21wb25lbnRzL2ltYWdlL2luc3RhbmNlcy9jbHZhN242YTUwMDBlMzU2aW04YWhyMHdu-L19jb21wb25lbnRzL2FydGljbGUvaW5zdGFuY2VzL2NsdmE3bXAzZDAwMGsyb3A1NHMyYWV1ZGM-scaled-e1713842902589.jpg"},
                {"University of Chicago", 4, "Известен своим вкладом в экономику и социологию.", "contact@uchicago.edu", "https://www.uchicago.edu", "https://cdn.britannica.com/39/160139-050-147B020E/Eckhart-Hall-campus-Ryerson-Physical-Laboratory-University.jpg"},
                {"Sorbonne University", 5, "Один из старейших университетов Европы.", "contact@sorbonne.fr", "https://www.sorbonne-universite.fr", "https://www.timeshighereducation.com/sites/default/files/sorbonne.jpg"},
                {"Humboldt University of Berlin", 6, "Исторический университет с акцентом на исследовательскую работу.", "contact@hu-berlin.de", "https://www.hu-berlin.de", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/86/Berlin-Mitte_Bebelplatz1_05-2014.jpg/533px-Berlin-Mitte_Bebelplatz1_05-2014.jpg"},
                {"University of Tokyo", 7, "Крупнейший университет Японии, лидер в области науки и технологий.", "contact@u-tokyo.ac.jp", "https://www.u-tokyo.ac.jp", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Yasuda_Auditorium_-_Tokyo_University_3.jpg/1200px-Yasuda_Auditorium_-_Tokyo_University_3.jpg"},
                {"University of Sydney", 8, "Один из ведущих университетов Австралии с разнообразными программами.", "contact@sydney.edu.au", "https://www.sydney.edu.au", "https://www.sydney.edu.au/content/dam/events/future-student-events/_YD_8486.jpg"},
                {"Moscow State University", 9, "Крупнейший и старейший университет России.", "contact@msu.ru", "https://www.msu.ru", "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/%D0%9C%D0%93%D0%A3%2C_%D0%B2%D0%B8%D0%B4_%D1%81_%D0%B2%D0%BE%D0%B7%D0%B4%D1%83%D1%85%D0%B0.jpg/1200px-%D0%9C%D0%93%D0%A3%2C_%D0%B2%D0%B8%D0%B4_%D1%81_%D0%B2%D0%BE%D0%B7%D0%B4%D1%83%D1%85%D0%B0.jpg"},
                {"University of Toronto", 10, "Известен своим исследовательским потенциалом и академическими программами.", "contact@utoronto.ca", "https://www.utoronto.ca", "https://d3d0lqu00lnqvz.cloudfront.net/media/media/UofT_cmh2315fl.jpg"}
        };

        for (Object[] university : universities) {
            ContentValues values = new ContentValues();
            values.put("name", (String) university[0]);
            values.put("city_id", (Integer) university[1]);
            values.put("description", (String) university[2]);
            values.put("contacts", (String) university[3]);
            values.put("website", (String) university[4]);
            values.put("image_url", (String) university[5]);
            db.insert("universities", null, values);
        }

        // Остальной код для специальностей и university_specialties остается без изменений
        String[] specialties = {"Computer Science", "Business Administration", "Engineering", "Medicine", "Law", "Economics", "Architecture", "Arts", "Physics", "Biology"};
        for (int i = 0; i < specialties.length; i++) {
            ContentValues values = new ContentValues();
            values.put("name", specialties[i]);
            db.insert("specialties", null, values);
        }

        int[][] univSpecs = {
                {1, 1}, {1, 3}, {2, 2}, {2, 5}, {3, 6}, {3, 4}, {4, 3}, {4, 7}, {5, 1}, {5, 8},
                {6, 9}, {6, 2}, {7, 10}, {7, 4}, {8, 6}, {8, 7}, {9, 5}, {9, 1}, {10, 3}, {10, 9}
        };
        for (int[] univSpec : univSpecs) {
            ContentValues values = new ContentValues();
            values.put("university_id", univSpec[0]);
            values.put("specialty_id", univSpec[1]);
            db.insert("university_specialties", null, values);
        }
    }

    public List<String> getAllCities() {
        List<String> cities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("cities", new String[]{"name"}, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    int nameIndex = cursor.getColumnIndex("name");
                    if (nameIndex != -1) {
                        cities.add(cursor.getString(nameIndex));
                    } else {
                        Log.e("DatabaseHelper", "Column 'name' not found in cursor");
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return cities;
    }
}