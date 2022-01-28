package com.teratail.q_tejy7wchc1rqto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.util.*;

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    //テストデータ
    //List<User> userList = new ArrayList<>();
    //userList.add(new User("titleA","2/10","14:00", "todoA1","todoA2",null,null,null));
    //userList.add(new User("titleB","2/20","10:00", "todoB1",null,"todoB3",null,null));
    //userList.add(new User("titleC","2/25","12:00", "todoC1","todoC2","todoC3","todoC4","todoC5"));
    DBHelper dbHelper = new DBHelper(this);
    dbHelper.deleteAll();
    dbHelper.insert(new User("DBtitleA","3/10","4:00", "DBtodoA1","DBtodoA2",null,null,null));
    dbHelper.insert(new User("DBtitleB","3/20","8:00", "DBtodoB1",null,"DBtodoB3",null,null));
    dbHelper.insert(new User("DBtitleC","2325","10:00", "DBtodoC1","DBtodoC2","DBtodoC3","DBtodoC4","DBtodoC5"));
    List<User> userList = dbHelper.getAll();

    ListView listView1 = findViewById(R.id.listView1);
    ListView listView2 = findViewById(R.id.listView2);

    UserAdapter userAdapter = new UserAdapter(userList);
    listView1.setAdapter(userAdapter);

    TodoAdapter todoAdapter = new TodoAdapter();
    listView2.setAdapter(todoAdapter);

    //list1 が選択されたら list2 に設定する
    listView1.setOnItemClickListener((adapterView, view, position, id) -> {
      todoAdapter.setUser((User)userAdapter.getItem(position));
    });
  }
}

class User {
  static class Todo {
    final int num;
    final String text;
    Todo(int num, String text) {
      this.num = num;
      this.text = text;
    }
  }
  final String title;
  final String date;
  final String time;
  final List<Todo> todoList = new ArrayList<>();
  User(String title, String date, String time, String... todos) {
    if(todos.length > 5) throw new IllegalArgumentException();
    this.title = title;
    this.date = date;
    this.time = time;

    for(int i=0; i<todos.length; i++) if(todos[i] != null) todoList.add(new Todo(i+1, todos[i]));
  }
}

class UserAdapter extends BaseAdapter {
  private List<User> userList;

  UserAdapter(List<User> userList) {
    this.userList = userList;
  }

  @Override
  public int getCount() {
    if(userList == null) return 0;
    return userList.size();
  }

  @Override
  public Object getItem(int position) {
    return userList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  private class ViewHolder {
    final TextView titleText;
    final TextView dateText;
    ViewHolder(View v) {
      titleText = v.findViewById(R.id.title);
      dateText = v.findViewById(R.id.date);
    }
  }

  @Override
  public View getView(int position, View view, ViewGroup viewGroup) {
    if(view == null) {
      view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview1_row, viewGroup, false);
      view.setTag(new ViewHolder(view));
    }
    ViewHolder vh = (ViewHolder)view.getTag();
    User user = userList.get(position);
    vh.titleText.setText(user.title);
    vh.dateText.setText(user.date);

    return view;
  }
}

class TodoAdapter extends BaseAdapter {
  private User user = null;

  void setUser(User user) {
    this.user = user;
    notifyDataSetChanged(); //ListView に表示内容が替わったことを知らせ、再表示を促す
  }

  @Override
  public int getCount() {
    if(user == null) return 0;
    return user.todoList.size();
  }

  @Override
  public Object getItem(int position) {
    return user.todoList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  private class ViewHolder {
    final TextView titleText;
    final TextView timeText;
    final TextView todoText;
    ViewHolder(View v) {
      titleText = v.findViewById(R.id.title);
      timeText = v.findViewById(R.id.time);
      todoText = v.findViewById(R.id.todo);
    }
  }

  @Override
  public View getView(int position, View view, ViewGroup viewGroup) {
    if(view == null) {
      view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listview2_row, viewGroup, false);
      view.setTag(new ViewHolder(view));
    }
    ViewHolder vh = (ViewHolder)view.getTag();
    vh.titleText.setText(user.title);
    vh.timeText.setText(user.time);
    vh.todoText.setText(user.todoList.get(position).num + "." + user.todoList.get(position).text);

    return view;
  }
}