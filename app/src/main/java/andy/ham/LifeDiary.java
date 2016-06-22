package andy.ham;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.lang.reflect.Field;

import andy.ham.DaoMaster.DevOpenHelper;

//继承来自listView
public class LifeDiary extends ListActivity {

	private SharedPreferences preferences;
	private  SharedPreferences pref;
	private SharedPreferences.Editor editor;
	// 检查密码
	private SharedPreferences textpassword = null;
	private String password = null;
	private boolean isSet = false;

	// 检查是否第一次进入应用

	public boolean isFirstIn = true;
	// 插入一条新纪录
	public static final int MENU_ITEM_INSERT = Menu.FIRST;
	// 编辑内容
	public static final int MENU_ITEM_EDIT = Menu.FIRST + 1;
	// private static final String[] PROJECTION =
	// new String[] { DiaryColumns._ID,
	// DiaryColumns.TITLE, DiaryColumns.CREATED };
	//
	// GreenDAO使用的变量
	private SQLiteDatabase db;

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private NoteDao noteDao;

	private Cursor cursor;


	private EditText setPass,checkpass;

	@Override

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.diary_list);
		InitDAO();
		InitList();
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isset=pref.getBoolean("isset",false);

		if(isset){
			checkPassword();


		}else
		{
			hello();
			editor=pref.edit();
			editor.putBoolean("isset",true);
			editor.commit();

		}


		// 初始化GreenDAO
		// 初始化List

	}



	/*
	 * 初始化DAO
	 */
	private void InitDAO() {
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db",
				null);
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();
		noteDao = daoSession.getNoteDao();

	}

	/*
	 * 初始化List
	 */
	private void InitList() {
		String textColumn = NoteDao.Properties.Title.columnName;
		String dateColunm = NoteDao.Properties.Date.columnName;
		String orderBy = dateColunm + " COLLATE LOCALIZED DESC";
		cursor = db.query(noteDao.getTablename(), noteDao.getAllColumns(),
				null, null, null, null, orderBy);
		String[] from = { textColumn, dateColunm };
		int[] to = { R.id.text1, R.id.created };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.diary_row, cursor, from, to);
		setListAdapter(adapter);
	}

	// 添加选择菜单
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_ITEM_INSERT, 0, R.string.menu_insert);
		return true;
	}

	// 添加选择菜单的选择事件
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// 插入一条数据
		case MENU_ITEM_INSERT:
			Intent intent0 = new Intent(this, DiaryEditor.class);
			intent0.setAction(DiaryEditor.INSERT_DIARY_ACTION);
			intent0.setData(getIntent().getData());
			startActivity(intent0);
			LifeDiary.this.finish();
			cursor.requery();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
		String mid = Long.toString(id);
		Log.d("id", mid);
		// Intent intent = new Intent();
		// startActivity(new Intent(DiaryEditor.EDIT_DIARY_ACTION, uri));
		Intent intent = new Intent(this, DiaryEditor.class);
		intent.setAction(DiaryEditor.EDIT_DIARY_ACTION);
		Bundle bundle = new Bundle();
		bundle.putLong("id", id);
		intent.putExtras(bundle);
		Log.d("id", mid);
		startActivity(intent);
		LifeDiary.this.finish();
		cursor.requery();
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		cursor.requery();
		super.onActivityResult(requestCode, resultCode, intent);
		// renderListView();
	}
	// @SuppressWarnings("deprecation")
	// private void renderListView() {
	// Cursor cursor = managedQuery(getIntent().getData(), PROJECTION,
	// null,null, DiaryColumns.DEFAULT_SORT_ORDER);
	//
	// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
	// R.layout.diary_row, cursor, new String[] { DiaryColumns.TITLE,
	// DiaryColumns.CREATED }, new int[] { R.id.text1,R.id.created });
	// setListAdapter(adapter);
	// }



	private  void  checkPassword(){

		textpassword=getSharedPreferences("pass",
				Context.MODE_PRIVATE);
		password=textpassword.getString("password","null");
		isSet=textpassword.getBoolean("isSet",false);

			final LayoutInflater factory=
					LayoutInflater.from(LifeDiary.this);
			final View textEntry= factory.inflate
					(R.layout.check_pass,null);
			AlertDialog.Builder builder =new AlertDialog.Builder(this)
					.setTitle("验证密码")
					.setView(textEntry)
					.setCancelable(false)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							checkpass= (EditText) textEntry.findViewById(R.id.check_pass);
							if (checkpass.getText().toString().trim().equals(password)) {

								try {
									Field field = dialog
											.getClass().getSuperclass().getDeclaredField("mShowing");
									field.setAccessible(true);
									field.set(dialog,true);
									Log.e("zzzz","01");
								} catch (Exception e) {
									e.printStackTrace();
								}
								dialog.dismiss();
							}else{

								try {
									Field field =dialog.getClass().getSuperclass().getDeclaredField("mShowing");
									field.setAccessible(true);
									field.set(dialog,false);
							}catch (Exception e){
									e.printStackTrace();
								}
								Toast.makeText(LifeDiary.this,"密码错误",Toast.LENGTH_SHORT).show();
								checkpass.setText("");
								}
							}


					});
			builder.create().show();
		}
	private void hello(){

		LayoutInflater factory=LayoutInflater.from(LifeDiary.this);
		final View textEntry=factory.inflate(R.layout.diary_password,null);
		AlertDialog.Builder builder=new AlertDialog.Builder(LifeDiary.this)
				.setTitle("KEY")
				.setView(textEntry)
				.setPositiveButton("设置",new DialogInterface.OnClickListener(){

					public  void onClick(DialogInterface dialog, int which){
						setPass= (EditText) textEntry.findViewById(R.id.set_pass);
						checkpass= (EditText) textEntry.findViewById(R.id.checkpass);
						if (setPass.getText().toString().trim().equals(checkpass.getText().toString().trim())&&
								checkpass.getText().toString().trim().equals(setPass.getText().toString().trim())){
							preferences=getSharedPreferences("pass", Context.MODE_PRIVATE);
							SharedPreferences.Editor editor=preferences.edit();
							editor.putBoolean("isSet",true);
							editor.putString("password",setPass.getText().toString().trim());
							editor.commit();
							dialog.dismiss();
							Toast.makeText(LifeDiary.this,"密码已保存",Toast.LENGTH_SHORT).show();
							Intent intent=new Intent(LifeDiary.this,LifeDiary.class);
							startActivity(intent);
							finish();


						}
					}
				})
				.setNegativeButton("canel",new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}


	}

