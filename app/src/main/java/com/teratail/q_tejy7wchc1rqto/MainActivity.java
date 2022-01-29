package com.teratail.q_tejy7wchc1rqto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.KeyEventDispatcher;

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
    long tab1 = dbHelper.insert(new TodoTab(0,"Tab1",1));
    long tab2 = dbHelper.insert(new TodoTab(0,"Tab2",2));
    long tab3 = dbHelper.insert(new TodoTab(0,"Tab3",3));
    dbHelper.insert(new User(0,tab1,"tab1titleA","3/10","4:00", "DBtodoA1","DBtodoA2",null,null,null));
    dbHelper.insert(new User(0,tab1,"tab1titleB","3/20","8:00", "DBtodoB1",null,"DBtodoB3",null,null));
    dbHelper.insert(new User(0,tab1,"tab1titleC","3/25","10:00", "DBtodoC1","DBtodoC2","DBtodoC3","DBtodoC4","DBtodoC5"));
    dbHelper.insert(new User(0,tab2,"tab2titleD","4/11","14:00", "DBtodoD1","DBtodoD2",null,null,null));
    dbHelper.insert(new User(0,tab3,"tab3titleE","5/2","9:15", "DBtodoE1","DBtodoE2","DBtodoE3",null,null));

    ListView listView1 = findViewById(R.id.listView1);
    ListView listView2 = findViewById(R.id.listView2);

    UserAdapter userAdapter = new UserAdapter();
    listView1.setAdapter(userAdapter);

    TodoAdapter todoAdapter = new TodoAdapter();
    listView2.setAdapter(todoAdapter);

    //list1 が選択されたら list2 に設定する
    listView1.setOnItemClickListener((adapterView, view, position, id) -> {
      todoAdapter.setUser((User)userAdapter.getItem(position));
    });

    setRadioTabs(dbHelper, (compoundButton, isChecked) -> {
      if(!isChecked) return;
      //ラジオボタンが選択変更されたら、各アダプタの内容を更新する
      long tabId = (long)compoundButton.getTag();
      userAdapter.setUserList(dbHelper.getUsers(tabId));
      todoAdapter.setUser(null);
    });
  }

  private List<RadioButton> radioButtonList = new ArrayList<>(); //一応保存

  private void setRadioTabs(DBHelper dbHelper, CompoundButton.OnCheckedChangeListener listener) {
    List<TodoTab> tabList = dbHelper.getAllTabs();
    RadioGroup radioGroup = findViewById(R.id.ragio);
    for(TodoTab tab : tabList) {
      RadioButton radioButton = createRadioButton(tab, listener);
      radioGroup.addView(radioButton);
      radioButtonList.add(radioButton);
    }
    radioGroup.check(radioButtonList.get(0).getId()); //この選択で各リスナも動く
  }

  private RadioButton createRadioButton(TodoTab tab, CompoundButton.OnCheckedChangeListener listener ) {
    RadioButton radioButton = new RadioButton(this);
    radioButton.setId(View.generateViewId());
    radioButton.setText(tab.title);
    radioButton.setTag(tab._id);
    radioButton.setOnCheckedChangeListener(listener);
    return radioButton;
  }
}

class TodoTab {
  long _id;
  String title;
  int order;
  TodoTab(long _id, String title, int order) {
    this._id = _id;
    this.title = title;
    this.order = order;
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
  long _id;
  final long tabId;
  final String title;
  final String date;
  final String time;
  final List<Todo> todoList = new ArrayList<>();
  User(long _id, long tabId, String title, String date, String time, String... todos) {
    if(todos.length > 5) throw new IllegalArgumentException();
    this._id = _id;
    this.tabId = tabId;
    this.title = title;
    this.date = date;
    this.time = time;

    for(int i=0; i<todos.length; i++) if(todos[i] != null) todoList.add(new Todo(i+1, todos[i]));
  }
}

class UserAdapter extends BaseAdapter {
  private List<User> userList;

  void setUserList(List<User> userList) {
    this.userList = userList;
    notifyDataSetChanged(); //ListView に表示内容が替わったことを知らせ、再表示を促す
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