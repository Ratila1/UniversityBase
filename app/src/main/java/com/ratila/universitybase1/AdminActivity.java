package com.ratila.universitybase1;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private DatabaseHelper dbHelper;
    private EditText nameEditText, cityEditText, descriptionEditText, contactsEditText, websiteEditText, imageUrlEditText;
    private Spinner specialtySpinner;
    private Button addButton, saveButton, deleteButton, addSpecialtyButton;
    private LinearLayout selectedSpecialtiesContainer;
    private ArrayList<String> selectedSpecialties = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize views
        tableLayout = findViewById(R.id.tableLayout);
        dbHelper = new DatabaseHelper(this);
        nameEditText = findViewById(R.id.nameEditText);
        cityEditText = findViewById(R.id.cityEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        contactsEditText = findViewById(R.id.contactsEditText);
        websiteEditText = findViewById(R.id.websiteEditText);
        imageUrlEditText = findViewById(R.id.imageUrlEditText); // Поле для URL картинки
        specialtySpinner = findViewById(R.id.specialtySpinner); // Spinner для выбора специальностей
        addButton = findViewById(R.id.addButton);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        addSpecialtyButton = findViewById(R.id.addSpecialtyButton); // Кнопка для добавления специальности
        selectedSpecialtiesContainer = findViewById(R.id.selectedSpecialtiesContainer); // Контейнер для выбранных специальностей

        // Apply fade-in animation
        applyFadeInAnimation(tableLayout);
        applyFadeInAnimation(addButton);
        applyFadeInAnimation(saveButton);
        applyFadeInAnimation(deleteButton);
        applyFadeInAnimation(nameEditText);
        applyFadeInAnimation(cityEditText);
        applyFadeInAnimation(descriptionEditText);
        applyFadeInAnimation(contactsEditText);
        applyFadeInAnimation(websiteEditText);
        applyFadeInAnimation(imageUrlEditText);
        applyFadeInAnimation(specialtySpinner);
        applyFadeInAnimation(addSpecialtyButton);

        // Load existing universities
        addExistingUniversitiesToTable();

        // Set button click listeners
        addButton.setOnClickListener(v -> addUniversity());
        saveButton.setOnClickListener(v -> saveChanges());
        deleteButton.setOnClickListener(v -> deleteUniversity());
        addSpecialtyButton.setOnClickListener(v -> addSpecialty()); // Добавление специальности
    }

    private void applyFadeInAnimation(View view) {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(500);
        view.startAnimation(fadeIn);
    }

    private void addExistingUniversitiesToTable() {
        List<University> universities = dbHelper.getAllUniversities();
        for (University university : universities) {
            TableRow row = new TableRow(this);
            TextView idTextView = new TextView(this);
            idTextView.setText(String.valueOf(university.getId()));
            row.addView(idTextView);

            TextView nameTextView = new TextView(this);
            nameTextView.setText(university.getName());
            row.addView(nameTextView);

            TextView cityTextView = new TextView(this);
            cityTextView.setText(dbHelper.getCityNameById(university.getCityId()));
            row.addView(cityTextView);

            TextView descriptionTextView = new TextView(this);
            descriptionTextView.setText(university.getDescription());
            row.addView(descriptionTextView);

            TextView contactsTextView = new TextView(this);
            contactsTextView.setText(university.getContacts());
            row.addView(contactsTextView);

            TextView websiteTextView = new TextView(this);
            websiteTextView.setText(university.getWebsite());
            row.addView(websiteTextView);

            row.setOnClickListener(v -> selectUniversity(university));
            tableLayout.addView(row);
        }
    }

    private University selectedUniversity;
    private void selectUniversity(University university) {
        nameEditText.setText(university.getName());
        cityEditText.setText(dbHelper.getCityNameById(university.getCityId()));
        descriptionEditText.setText(university.getDescription());
        contactsEditText.setText(university.getContacts());
        websiteEditText.setText(university.getWebsite());
        imageUrlEditText.setText(university.getImageUrl()); // Загрузка URL картинки

        // Загрузка специальностей университета
        loadSelectedSpecialties(university.getId());

        selectedUniversity = university;
    }

    private void loadSelectedSpecialties(int universityId) {
        selectedSpecialties.clear();
        selectedSpecialtiesContainer.removeAllViews();

        List<Specialty> specialties = dbHelper.getSpecialtiesByUniversityId(universityId);
        for (Specialty specialty : specialties) {
            selectedSpecialties.add(specialty.getName());

            TextView specialtyTextView = new TextView(this);
            specialtyTextView.setText(specialty.getName());
            specialtyTextView.setPadding(10, 5, 10, 5);
            specialtyTextView.setBackgroundResource(android.R.drawable.editbox_background_normal);
            selectedSpecialtiesContainer.addView(specialtyTextView);
        }
    }

    private void addUniversity() {
        String name = nameEditText.getText().toString();
        String cityName = cityEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String contacts = contactsEditText.getText().toString();
        String website = websiteEditText.getText().toString();
        String imageUrl = imageUrlEditText.getText().toString(); // Получение URL картинки

        if (name.isEmpty() || cityName.isEmpty() || description.isEmpty() || contacts.isEmpty() || website.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT).show();
            return;
        }

        int cityId = dbHelper.getCityIdByName(cityName);
        if (cityId == -1) {
            cityId = dbHelper.addCity(cityName);
        }

        University newUniversity = new University(0, name, cityId, description, contacts, website, imageUrl);
        long universityId = dbHelper.addUniversity(newUniversity);

        // Сохраняем связь между университетом и специальностями
        saveUniversitySpecialties(universityId);

        // Clear input fields after adding
        clearInputFields();

        // Update table with new data
        tableLayout.removeAllViews();
        addExistingUniversitiesToTable();
    }

    private void saveChanges() {
        if (selectedUniversity == null) {
            Toast.makeText(this, "Выберите университет для редактирования.", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameEditText.getText().toString();
        String cityName = cityEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String contacts = contactsEditText.getText().toString();
        String website = websiteEditText.getText().toString();
        String imageUrl = imageUrlEditText.getText().toString(); // Получение URL картинки

        if (name.isEmpty() || cityName.isEmpty() || description.isEmpty() || contacts.isEmpty() || website.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT).show();
            return;
        }

        int cityId = dbHelper.getCityIdByName(cityName);
        if (cityId == -1) {
            cityId = dbHelper.addCity(cityName);
        }

        selectedUniversity.setName(name);
        selectedUniversity.setCityId(cityId);
        selectedUniversity.setDescription(description);
        selectedUniversity.setContacts(contacts);
        selectedUniversity.setWebsite(website);
        selectedUniversity.setImageUrl(imageUrl);

        dbHelper.updateUniversity(selectedUniversity);

        // Обновляем связь между университетом и специальностями
        saveUniversitySpecialties(selectedUniversity.getId());

        // Update table with new data
        tableLayout.removeAllViews();
        addExistingUniversitiesToTable();
    }

    private void deleteUniversity() {
        if (selectedUniversity == null) {
            Toast.makeText(this, "Выберите университет для удаления.", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.deleteUniversity(selectedUniversity.getId());

        // Clear input fields after deletion
        clearInputFields();

        // Update table with new data
        tableLayout.removeAllViews();
        addExistingUniversitiesToTable();
    }

    private void addSpecialty() {
        String selectedSpecialty = specialtySpinner.getSelectedItem().toString();

        if (!selectedSpecialties.contains(selectedSpecialty)) {
            selectedSpecialties.add(selectedSpecialty);

            TextView specialtyTextView = new TextView(this);
            specialtyTextView.setText(selectedSpecialty);
            specialtyTextView.setPadding(10, 5, 10, 5);
            specialtyTextView.setBackgroundResource(android.R.drawable.editbox_background_normal);
            selectedSpecialtiesContainer.addView(specialtyTextView);
        } else {
            Toast.makeText(this, "Эта специальность уже добавлена.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUniversitySpecialties(long universityId) {
        // Удаляем старые связи
        dbHelper.deleteUniversitySpecialties(universityId);

        // Создаем новые связи
        for (String specialtyName : selectedSpecialties) {
            int specialtyId = dbHelper.getSpecialtyIdByName(specialtyName);
            if (specialtyId != -1) {
                dbHelper.addUniversitySpecialty(universityId, specialtyId);
            }
        }
    }

    private void deleteUniversitySpecialties(long universityId) {
        dbHelper.deleteUniversitySpecialties(universityId);
    }

    private void clearInputFields() {
        nameEditText.setText("");
        cityEditText.setText("");
        descriptionEditText.setText("");
        contactsEditText.setText("");
        websiteEditText.setText("");
        imageUrlEditText.setText("");
        selectedSpecialties.clear();
        selectedSpecialtiesContainer.removeAllViews();
    }
}