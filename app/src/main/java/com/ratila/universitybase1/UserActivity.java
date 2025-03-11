package com.ratila.universitybase1;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.squareup.picasso.Picasso;

public class UserActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private DatabaseHelper dbHelper;
    private List<University> allUniversities;
    private List<University> filteredUniversities;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "FilterPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        tableLayout = findViewById(R.id.tableLayout);
        dbHelper = new DatabaseHelper(this);
        allUniversities = dbHelper.getAllUniversities();
        filteredUniversities = new ArrayList<>(allUniversities);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        loadFilters();
        addExistingUniversitiesToTable(filteredUniversities);

        EditText searchEditText = findViewById(R.id.searchEditText);
        Button searchButton = findViewById(R.id.searchButton);
        Button filterButton = findViewById(R.id.filterButton);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String[] queries = s.toString().toLowerCase().split("\\s+");
                filteredUniversities.clear();
                for (University university : allUniversities) {
                    boolean matches = true;
                    for (String query : queries) {
                        if (!university.getName().toLowerCase().contains(query) &&
                                !dbHelper.getCityNameById(university.getCityId()).toLowerCase().contains(query)) {
                            matches = false;
                            break;
                        }
                    }
                    if (matches) {
                        filteredUniversities.add(university);
                    }
                }
                updateTable(filteredUniversities);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchButton.setOnClickListener(v -> {
            // Можно добавить дополнительную логику для кнопки Поиск, если нужно
        });

        filterButton.setOnClickListener(v -> showFilterDialog());
    }

    private void addExistingUniversitiesToTable(List<University> universities) {
        tableLayout.removeAllViews();

        // Добавление заголовка таблицы
        TableRow headerRow = new TableRow(this);
        TextView nameHeader = createTableHeader("Название");
        TextView cityHeader = createTableHeader("Город");
        TextView infoHeader = createTableHeader("Доп информация");

        headerRow.addView(nameHeader);
        headerRow.addView(cityHeader);
        headerRow.addView(infoHeader);
        tableLayout.addView(headerRow);

        applyFadeInAnimation(headerRow); // Анимация для заголовка

        // Добавление строк данных
        for (University university : universities) {
            TableRow row = new TableRow(this);

            // Название университета
            TextView nameTextView = createTableCell(university.getName());
            row.addView(nameTextView);

            // Город университета
            TextView cityTextView = createTableCell(dbHelper.getCityNameById(university.getCityId()));
            row.addView(cityTextView);

            // Кнопка "Инфо"
            Button infoButton = new Button(this);
            infoButton.setText("Инфо");
            infoButton.setPadding(8, 8, 8, 8);
            infoButton.setOnClickListener(v -> showDetailsDialog(university));
            row.addView(infoButton);

            tableLayout.addView(row);
            applyFadeInAnimation(row); // Анимация для строки
            applyFadeInAnimation(infoButton); // Анимация для кнопки
        }
    }

    private void updateTable(List<University> universities) {
        tableLayout.removeAllViews();
        addExistingUniversitiesToTable(universities);
    }

    private TextView createTableHeader(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }

    private TextView createTableCell(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите фильтр");

        List<String> cities = dbHelper.getAllCities();
        boolean[] selectedCities = new boolean[cities.size()];
        for (int i = 0; i < cities.size(); i++) {
            selectedCities[i] = prefs.getBoolean(cities.get(i), false);
        }

        builder.setMultiChoiceItems(cities.toArray(new String[0]), selectedCities, (dialog, which, isChecked) -> {
            selectedCities[which] = isChecked;
        });

        builder.setPositiveButton("Применить", (dialog, which) -> {
            saveFilters(selectedCities);
            applyFilters(selectedCities);
        });

        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void loadFilters() {
        List<String> cities = dbHelper.getAllCities();
        boolean[] selectedCities = new boolean[cities.size()];
        for (int i = 0; i < cities.size(); i++) {
            selectedCities[i] = prefs.getBoolean(cities.get(i), false);
        }
        applyFilters(selectedCities);
    }

    private void saveFilters(boolean[] selectedCities) {
        List<String> cities = dbHelper.getAllCities();
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < cities.size(); i++) {
            editor.putBoolean(cities.get(i), selectedCities[i]);
        }
        editor.apply();
    }

    private void applyFilters(boolean[] selectedCities) {
        List<String> cities = dbHelper.getAllCities();
        filteredUniversities.clear();

        boolean anyCitySelected = false;
        for (boolean selected : selectedCities) {
            if (selected) {
                anyCitySelected = true;
                break;
            }
        }

        if (anyCitySelected) {
            for (University university : allUniversities) {
                String cityName = dbHelper.getCityNameById(university.getCityId());
                for (int i = 0; i < selectedCities.length; i++) {
                    if (selectedCities[i] && cityName.equals(cities.get(i))) {
                        filteredUniversities.add(university);
                        break;
                    }
                }
            }
        } else {
            filteredUniversities.addAll(allUniversities);
        }

        updateTable(filteredUniversities);
    }

    private void showDetailsDialog(University university) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(university.getName());

        // Создаем кастомный вид для диалогового окна
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_details, null);
        builder.setView(dialogView);

        // Находим элементы в кастомном layout
        ImageView imageView = dialogView.findViewById(R.id.universityImage);
        TextView descriptionValueTextView = dialogView.findViewById(R.id.descriptionValueTextView);
        TextView siteValueTextView = dialogView.findViewById(R.id.siteValueTextView);
        TextView contactsValueTextView = dialogView.findViewById(R.id.contactsValueTextView);
        TextView specialtiesValueTextView = dialogView.findViewById(R.id.specialtiesValueTextView);

        // Установка значений
        descriptionValueTextView.setText(university.getDescription());
        siteValueTextView.setText(university.getWebsite());
        contactsValueTextView.setText(university.getContacts());

        // Получение и отображение специальностей
        List<Specialty> specialties = dbHelper.getSpecialtiesByUniversityId(university.getId());
        if (!specialties.isEmpty()) {
            StringBuilder specialtiesText = new StringBuilder();
            for (Specialty specialty : specialties) {
                specialtiesText.append("- ").append(specialty.getName()).append("\n");
            }
            specialtiesValueTextView.setText(specialtiesText.toString());
        } else {
            specialtiesValueTextView.setText("Нет доступных специальностей");
        }

        // Загрузка изображения с кэшированием через PicassoCache
        if (university.getImageUrl() != null && !university.getImageUrl().isEmpty()) {
            PicassoCache.getInstance(this)
                    .load(university.getImageUrl())
                    .placeholder(R.drawable.placeholder) // Placeholder изображение
                    .error(R.drawable.error) // Изображение для ошибки
                    .resize(300, 200) // Установите желаемый размер
                    .centerCrop()
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.placeholder); // Если URL нет
        }

        builder.setPositiveButton("OK", null);
        builder.show();
    }


    private void applyFadeInAnimation(View view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000); // Длительность 500 мс
        view.startAnimation(fadeIn);
    }

}