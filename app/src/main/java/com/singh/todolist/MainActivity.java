package com.singh.todolist;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        taskList = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        taskAdapter = new TaskAdapter(taskList, 
            this::showEditDialog, 
            this::deleteTask
        );
        recyclerView.setAdapter(taskAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> showAddDialog());

        loadTasks();
    }

    private void loadTasks() {
        taskList.clear();
        Cursor cursor = databaseHelper.getAllTasks();
        while (cursor.moveToNext()) {
            taskList.add(new Task(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getString(3)
            ));
        }
        cursor.close();
        taskAdapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_task, null);
        EditText taskEdit = view.findViewById(R.id.taskEdit);

        builder.setView(view)
               .setTitle(R.string.add_task)
               .setPositiveButton(R.string.save, (dialog, which) -> {
                   String task = taskEdit.getText().toString();
                   if (!task.isEmpty()) {
                       databaseHelper.insertTask(task);
                       loadTasks();
                   }
               })
               .setNegativeButton(R.string.cancel, null)
               .show();
    }

    private void showEditDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_task, null);
        EditText taskEdit = view.findViewById(R.id.taskEdit);
        taskEdit.setText(task.getTask());

        builder.setView(view)
               .setTitle(R.string.edit_task)
               .setPositiveButton(R.string.save, (dialog, which) -> {
                   String updatedTask = taskEdit.getText().toString();
                   if (!updatedTask.isEmpty()) {
                       databaseHelper.updateTask(String.valueOf(task.getId()), updatedTask);
                       loadTasks();
                   }
               })
               .setNegativeButton(R.string.cancel, null)
               .show();
    }

    private void deleteTask(Task task) {
        databaseHelper.deleteTask(String.valueOf(task.getId()));
        int position = taskList.indexOf(task);
        taskList.remove(position);
        taskAdapter.notifyItemRemoved(position);

        Snackbar.make(findViewById(R.id.main), R.string.task_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    databaseHelper.insertTask(task.getTask());
                    loadTasks();
                }).show();
    }
}