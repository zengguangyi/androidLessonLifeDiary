package andy.ham;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.lang.reflect.Field;

import andy.ham.Fields.DiaryColumns;
//�̳�����listView
public class LifeDiary extends ListActivity {
	// ����һ���¼�¼
	public static final int MENU_ITEM_INSERT = Menu.FIRST;
	// �༭����

	private SharedPreferences pref;
	private SharedPreferences preferences;
	private EditText setPass,checkpass;
	private SharedPreferences.Editor editor;

	private SharedPreferences textpassword = null;
	private String password = null;
	private boolean isSet = false;


	public static final int MENU_ITEM_EDIT = Menu.FIRST + 1;
	public static final int MENU_ITEM_DELETE = Menu.FIRST + 2;
	private static final String[] PROJECTION = 
		new String[] { DiaryColumns._ID,
			DiaryColumns.TITLE, DiaryColumns.CREATED };
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_list);


		pref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isset=pref.getBoolean("isset", false);

		if (isset){
			if(IsFirst.IsChecked ==false)
				checkPassword();
		}else
		{
			setPassword();
			editor=pref.edit();
			editor.putBoolean("isset",true);
			editor.commit();
		}



        
        Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(DiaryColumns.CONTENT_URI);
		}
		Cursor cursor = managedQuery(getIntent().getData(), 
				PROJECTION, null,null, DiaryColumns.DEFAULT_SORT_ORDER);
		/*SimpleCursorAdapter�������һ���α���е�ListView��
		��ʹ���Զ����layout��ʾÿ����Ŀ��
		SimpleCursorAdapter�Ĵ�������Ҫ����
		��ǰ�������ġ�һ��layout��Դ��һ���α����������
		�������飺1ʹ�õ��е����֣�2����ͬ��С���������View�е���ԴID
		������ʾ��Ӧ�е�����ֵ��*/
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.diary_row, cursor, new String[]
				{ DiaryColumns.TITLE,DiaryColumns.CREATED }, 
				new int[] { R.id.text1,R.id.created });
		setListAdapter(adapter);
    }   
    //���ѡ��˵�
    public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, MENU_ITEM_INSERT, 0, R.string.menu_insert);
		menu.add(Menu.NONE, MENU_ITEM_DELETE, 0, R.string.menu_delete);
		return true;
	}
    //���ѡ��˵���ѡ���¼�
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// ����һ������
		case MENU_ITEM_INSERT:
			Intent intent0 = new Intent(this, DiaryEditor.class);
			intent0.setAction(DiaryEditor.INSERT_DIARY_ACTION);
			intent0.setData(getIntent().getData());
			startActivity(intent0);
			return true;
			// �༭��ǰ��������ͨ�������ռ���ʵ��
			// ɾ����ǰ����
		case MENU_ITEM_DELETE:
			Uri uri = ContentUris.withAppendedId(getIntent().getData(),
					getListView().getSelectedItemId());
			getContentResolver().delete(uri, null, null);
			renderListView();
		}
		return super.onOptionsItemSelected(item);
	} 
	protected void onListItemClick
	(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
		startActivity(new Intent(DiaryEditor.EDIT_DIARY_ACTION, uri));
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		//renderListView();
	}
	private void renderListView() {
		Cursor cursor = managedQuery(getIntent().getData(), PROJECTION,
				null,null, DiaryColumns.DEFAULT_SORT_ORDER);

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
			R.layout.diary_row, cursor, new String[] { DiaryColumns.TITLE,
			DiaryColumns.CREATED }, new int[] { R.id.text1,R.id.created });
		setListAdapter(adapter);
	}





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
				.setTitle("��֤����")
				.setView(textEntry)
				.setCancelable(false)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						checkpass= (EditText) textEntry.findViewById(R.id.check_pass);
						if (checkpass.getText().toString().trim().equals(password)) {

							try {
								Field field = dialog
										.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog,true);
								Log.e("zzzz", "01");
							} catch (Exception e) {
								e.printStackTrace();
							}
							dialog.dismiss();
							IsFirst.IsChecked=true;
							Log.e("已经验证",IsFirst.IsChecked+"");
						}else{

							try {
								Field field =dialog.getClass().getSuperclass().getDeclaredField("mShowing");
								field.setAccessible(true);
								field.set(dialog,false);
							}catch (Exception e){
								e.printStackTrace();
							}
							Toast.makeText(LifeDiary.this, "密码错误请重新输入", Toast.LENGTH_SHORT).show();
							checkpass.setText("");
						}
					}


				});
		builder.create().show();

	}
	private void setPassword(){

		LayoutInflater factory=LayoutInflater.from(LifeDiary.this);
		final View textEntry=factory.inflate(R.layout.diary_password, null);
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
							editor.putBoolean("isset",true);
							editor.putString("password",setPass.getText().toString().trim());
							editor.commit();
							dialog.dismiss();
							Toast.makeText(LifeDiary.this,"密码已经保存",Toast.LENGTH_SHORT).show();
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
