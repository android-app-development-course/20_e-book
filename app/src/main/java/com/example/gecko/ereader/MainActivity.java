package com.example.gecko.ereader;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gecko.ereader.db.BookList;
import com.example.gecko.ereader.util.Fileutil;
import com.example.searcher.FileSearcher;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static com.example.gecko.ereader.R.id.action_settings;
import static java.lang.Integer.valueOf;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;//页卡内容
    private ImageView scrollbar;// 动画图片
    private int offset = 0;// 动画图片偏移量
    private int bmpW;// 动画图片宽度
    @SuppressLint("StaticFieldLeak")
    List<BookList> bookLists = new ArrayList<>();
    private SaveBookToSqlLiteTask mSaveBookToSqlLiteTask;
    private static boolean isExit = false;
    private MSimpleAdapter simpleAdapter;
    List<number> list21;
    private MyHepler myHelper;
    private EditText path;
    private Intent intent;


    private void InitTextView() {
        TextView t1 = findViewById(R.id.app_book);
        TextView t2 = findViewById(R.id.app_discover);

        TextPaint tp = t1.getPaint();
        tp.setFakeBoldText(true);
        TextPaint tp1 = t2.getPaint();
        tp1.setFakeBoldText(true);



        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }

    private void InitViewPager() {
        viewPager = findViewById(R.id.viewPager);
        LayoutInflater mInflater = getLayoutInflater();
        @SuppressLint("InflateParams") View view1 = mInflater.inflate(R.layout.app_book, null);
        @SuppressLint("InflateParams") View view2 = mInflater.inflate(R.layout.app_discover, null);
        final List<View> list = new ArrayList<>();
        list.add(view1);
        list.add(view2);
        viewPager.setAdapter(new MyPagerAdapter(list));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        ListView mainlv = view1.findViewById(R.id.mainlv);
        bookLists = DataSupport.findAll(BookList.class);
        simpleAdapter= new MSimpleAdapter(MainActivity.this, bookLists);
        mainlv.setAdapter(simpleAdapter);
        SharedPreferences sp=MainActivity.this.getSharedPreferences("data",Context.MODE_PRIVATE);
        String phone=sp.getString("phone",null);
        TextView riji =view2.findViewById(R.id.riji);
        TextView boollogin=view2.findViewById(R.id.boollogin);
        ListView lv21 =view2.findViewById(R.id.lv21);
        FloatingActionButton fab = view2.findViewById(R.id.fab);

        myHelper=new MyHepler(this,"informatione",null,1);
        list21= new ArrayList<>();
        SQLiteDatabase db = myHelper.getWritableDatabase();
        Cursor cursor = db.query("informatione", null, null, null, null, null, null);
        while (cursor.moveToNext()){
            String ming =cursor.getString(cursor.getColumnIndex("name"));
            String ming1 =cursor.getString(cursor.getColumnIndex("name1"));
            number n= new number(ming,ming1);
            list21.add(n);
        }
        lv21.setAdapter(new MyAdapter());
        db.close();
        lv21.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                number info = list21.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("name", info.getName());
                bundle.putString("name1", info.getName1());
                Intent intent = new Intent(MainActivity.this,Show.class);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        db.close();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(MainActivity.this,Add.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        if(phone!=null){
            boollogin.setVisibility(View.GONE);
            lv21.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            riji.setVisibility(View.VISIBLE);
        }




        mainlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final BookList bookList = bookLists.get(position);
                ReadActivity.openBook(bookList,MainActivity.this);
            }
        });
        mainlv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确定删除?");
                builder.setTitle("提示");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        final BookList bookList = bookLists.get(position);
                        final String path = bookList.getBookpath();
                        bookLists.remove(position);
                        DataSupport.deleteAll(BookList.class, "bookpath = ?", path);
                        bookLists = DataSupport.findAll(BookList.class);
                        simpleAdapter.setBookList(bookLists);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
                return true;
            }
        });

        view1.findViewById(R.id.example_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = ".txt";
                long minSize = 1048576;
                long maxSize = 104857600;

                FileSearcher fileSearcher = new FileSearcher(MainActivity.this);
                fileSearcher
                        .withKeyword(content)
                        .withSizeLimit(minSize,maxSize)
                        .search(new FileSearcher.FileSearcherCallback() {
                            @Override
                            public void onSelect(List<File> files) {
                                for(File file: files){
                                    BookList bookList = new BookList();
                                    String bookName = Fileutil.getFileNameNoEx(file.getName());
                                    bookList.setBookname(bookName);
                                    bookList.setBookpath(file.getAbsolutePath());
                                    bookLists.add(bookList);
                                }
                                DataSupport.saveAll(bookLists);
                                mSaveBookToSqlLiteTask = new SaveBookToSqlLiteTask();
                                mSaveBookToSqlLiteTask.execute(bookLists);
                                simpleAdapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "you have selected "+files.size()+" file(s).", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }


    @Override
    protected void onRestart(){
        super.onRestart();
        bookLists = DataSupport.findAll(BookList.class);
        simpleAdapter.setBookList(bookLists);
    }

    private class SaveBookToSqlLiteTask extends AsyncTask<List<BookList>,Void,Integer> {
        private static final int FAIL = 0;
        private static final int SUCCESS = 1;
        private static final int REPEAT = 2;
        private BookList repeatBookList;

        @Override
        protected Integer doInBackground(List<BookList>... params) {
            List<BookList> bookLists = params[0];
            for (BookList bookList : bookLists){
                List<BookList> books = DataSupport.where("bookpath = ?", bookList.getBookpath()).find(BookList.class);
                if (books.size() > 0){
                    repeatBookList = bookList;
                    return REPEAT;
                }
            }

            try {
                DataSupport.saveAll(bookLists);
            } catch (Exception e){
                e.printStackTrace();
                return FAIL;
            }
            return SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result){
                case FAIL:
                    break;
                case SUCCESS:
                    break;
                case REPEAT:
                    break;
            }

        }
    }


    private void InitImageView() {
        scrollbar = findViewById(R.id.scrollbar);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.p1)
                .getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        scrollbar.setImageMatrix(matrix);// 设置动画初始位置
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    break;
                case 1:
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    break;
            }
            //arg0为切换到的页的编码
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            assert animation != null;
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
            //滚动条开始动画
            scrollbar.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }


    public class MyPagerAdapter extends PagerAdapter {

        List<View> mListViews;

        MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1));

            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }


    }

    protected void onDestroy() {

        super.onDestroy();
        Log.i("MainActivity","destroy");
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initTitleOne();
        InitTextView();
        InitImageView();
        InitViewPager();

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        int version = valueOf(android.os.Build.VERSION.SDK);
        //根据不同的id点击不同按钮控制activity需要做的事件
        switch (item.getItemId())
        {
            case action_settings:
                //事件
                if(item.getTitle().toString().length()!=11) {
                    Intent intent = new Intent(MainActivity.this,TwoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                }else{
                    SharedPreferences sp=MainActivity.this.getSharedPreferences("data",Context.MODE_PRIVATE);
                    final SharedPreferences.Editor edit=sp.edit();
                    new android.support.v7.app.AlertDialog.Builder(this)
                            .setTitle("退出登录？").
                            setPositiveButton(getString(com.example.searcher.R.string.file_searcher_confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    edit.clear();
                                    edit.commit();
                                    item.setTitle(R.string.app_login);
                                }
                            }).setNegativeButton(getString(com.example.searcher.R.string.file_searcher_cancel),null)
                            .create().show();
                }
                break;
            case R.id.action_settings1:
                //事件
                String content = ".txt";
                long minSize = 1048576;
                long maxSize = 104857600;

                FileSearcher fileSearcher = new FileSearcher(MainActivity.this);
                fileSearcher
                        .withKeyword(content)
                        .withSizeLimit(minSize,maxSize)
                        .search(new FileSearcher.FileSearcherCallback() {
                            @Override
                            public void onSelect(List<File> files) {
                                for(File file: files){
                                    BookList bookList = new BookList();
                                    String bookName = Fileutil.getFileNameNoEx(file.getName());
                                    bookList.setBookname(bookName);
                                    bookList.setBookpath(file.getAbsolutePath());
                                    bookLists.add(bookList);
                                }
                                DataSupport.saveAll(bookLists);
                                mSaveBookToSqlLiteTask = new SaveBookToSqlLiteTask();
                                mSaveBookToSqlLiteTask.execute(bookLists);
                                simpleAdapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "you have selected "+files.size()+" file(s).", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
            case R.id.action_settings3:
                //事件
                Intent intent3 = new Intent(MainActivity.this,settingActivity.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent3);
                overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
                break;
            default:
                //对没有处理的事件，交给父类来处理
                return super.onOptionsItemSelected(item);
        }
        return true;
    }





    private void initTitleOne() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.ic_book_black_24dp1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        SharedPreferences sp=MainActivity.this.getSharedPreferences("data",Context.MODE_PRIVATE);
        String phone=sp.getString("phone",null);
        if(phone!=null) {
            menu.findItem(action_settings).setTitle(phone);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    @SuppressLint("PrivateApi") Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    private class MSimpleAdapter extends BaseAdapter {
        private List<BookList> bilist;


        public MSimpleAdapter(Context context, List<BookList> bilist){
            Context mContex = context;
            this.bilist = bilist;
        }
        @Override
        public int getCount() {
            return bilist.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void setBookList(List<BookList> bookLists){
            this.bilist = bookLists;
            notifyDataSetChanged();
        }



        @Override
        public View getView(int position, View convertview, ViewGroup parent) {
            View view =View.inflate(MainActivity.this,R.layout.pes,null);
            TextView mTextView= view.findViewById(R.id.txt_tv);
            mTextView.setText(bilist.get(position).getBookname());
            return view;
        }
    }
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return list21.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(MainActivity.this);
            tv.setTextSize(18);
            number u=list21.get(position);
            tv.setText(u.toString());
            return tv;
        }
    }

}
