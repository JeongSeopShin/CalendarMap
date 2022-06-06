package com.example.calendarmap;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class WeekCalendarFragment extends Fragment {
    // 캘린더 선언
    Calendar mCal;
    int cellnum;

    //실제 사용자가 터치한 뷰를 가르키는 변수
    private int mTouchStartView = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private DBHelper mDbHelper;
    // TODO: Rename and change types of parameters
    private int mParam1;
    private int mParam2;
    private int mParam3;
    private int mParam4;
    boolean go1 = false;
    boolean go2 = false;
    int dayNumber;
    int hour;
    public WeekCalendarFragment() {
        // Required empty public constructor
    }

    public static WeekCalendarFragment newInstance(int year, int month, int day, int day2) {
        WeekCalendarFragment fragment = new WeekCalendarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, year);
        args.putInt(ARG_PARAM2, month);
        args.putInt(ARG_PARAM3, day);
        args.putInt(ARG_PARAM4, day2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
            mParam3 = getArguments().getInt(ARG_PARAM3);
            mParam4 = getArguments().getInt(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDbHelper = new DBHelper(this.getContext());
        int year = mParam1;
        int month = mParam2;
        View rootView = inflater.inflate(R.layout.fragment_week_calendar, container, false);
        ArrayList<String> dayList = new ArrayList<String>();
        ArrayList<String> voidcell = new ArrayList<String>();
        mCal = Calendar.getInstance();
        mCal.set(Integer.parseInt(String.valueOf(year)), Integer.parseInt(String.valueOf(month)) - 1, 1);
        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        int dayMax = mCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int dm = (mParam3*7+1-dayNum)/dayMax;
        int day = mParam3*7%42;
        System.out.println(day);
        if(day<7)
            day=0;
        int day2 = mParam4;
        int count=0;

        if(day<dayNum) {
            for (int i = 1; i < dayNum; i++) {
                dayList.add("");
                count++;
            }
            for (int i = 1; i < 8 - count; i++){
                dayList.add(String.valueOf(i));
                System.out.println("month"+month+"dayNum:"+dayNum+"day:"+(i) +"dayMAx:"+dayMax);
            }
        }
        else{
            for (int i = dayNum; i > dayNum-7; i--) { // 최대 일 수만큼 dayList에 요소를 추가한다.
                if((day-i+2)>dayMax)
                    dayList.add("");
                else
                    dayList.add(String.valueOf(day-i+2));
                System.out.println("month"+month+"dayNum:"+dayNum+"day:"+(day-i+2) +"dayMAx:"+dayMax);

            }
        }

        // 해당 달의 최대 일 수를 구하기 위해 .getActualMaximum(Calendar.DAY_OF_MONTH) 함수를 사용

        ArrayAdapter<String> adapt
                = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_week,R.id.item_gridview2,
                dayList);
        // 기존에 simple_list_item_1 리소스를 사용하였으나 텍스트 정렬을 위해
        // item_month 레이아웃을 만들어 그 내부에 만든 item_gridview를 사용하였음

        // id를 바탕으로 화면 레이아웃에 정의된 GridView 객체 로딩
        GridView gridview = rootView.findViewById(R.id.gridview2);
        // 어댑터를 GridView 객체에 연결
        gridview.setAdapter(adapt);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                cellnum = position+1;
                if(dayList.get(position) != "") {
                    dayNumber = Integer.parseInt(dayList.get(position));
                    go1 = true;
                }
            }
        });
        // 시간을 표현할 데이터 원본 준비
        String[] items = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};

        // 어댑터 준비 (배열 객체 이용, simple_list_item_1 리소스 사용)
        ArrayAdapter<String> adapt2
                = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_datetime,R.id.datetime,
                items);

        // 어댑터 연결
        ListView listview = rootView.findViewById(R.id.listView);
        listview.setAdapter(adapt2);
        for(int i=0; i<24; i++){
            for(int j=0; j<7; j++) {
                //해당 요일의 시간칸별로 SQL포인터를 이동시켜서 스케줄이 있으면 칸에 표시함
                Cursor cursor = mDbHelper.getHourUsersBySQL(String.valueOf(year), String.valueOf(month), String.valueOf(dayList.get(j)), String.valueOf(i));
                if (cursor.moveToNext()){
                    voidcell.add(cursor.getString(cursor.getColumnIndex(UserContract.Users.SCHEDULE_TITLE)));
                }
                else
                    voidcell.add("");
            }
        }
        ArrayAdapter<String> adapt3
                = new ArrayAdapter<String>(
                getActivity(),
                R.layout.item_hour, R.id.item_hour,
                voidcell){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                TextView tv_cell = (TextView) super.getView(position,convertView,parent);
                tv_cell.setBackgroundColor(Color.WHITE);
                return tv_cell;
            }
        };
        // 기존에 simple_list_item_1 리소스를 사용하였으나 텍스트 정렬을 위해
        // item_month 레이아웃을 만들어 그 내부에 만든 item_gridview를 사용함

        // id를 바탕으로 화면 레이아웃에 정의된 GridView 객체 로딩
        GridView gridview2 = rootView.findViewById(R.id.gridview3);
        // 어댑터를 GridView 객체에 연결
        gridview2.setAdapter(adapt3);

        gridview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                // 해당 칸에 스케줄이 있으면 해당 스케줄 데이터를 스케줄액티비티에 전송함
                if(cellnum > 0)
                    Toast.makeText(getActivity(),"position="+position,Toast.LENGTH_SHORT).show();
                hour = position/7;
                System.out.println(hour);
                go2 = true;
                if(adapt3.getItem(position) != "") {
                    Cursor cursor = mDbHelper.getHourUsersBySQL(String.valueOf(year), String.valueOf(month), dayList.get(position%7), String.valueOf(hour));
                    cursor.moveToNext();
                    Intent intent = new Intent(getActivity(), schedule.class);
                    intent.putExtra("year", year);
                    intent.putExtra("month", month);
                    intent.putExtra("day", Integer.parseInt(dayList.get(position%7)));
                    intent.putExtra("hour", hour);
                    startActivity(intent);
                }
            }
        });

        // 그리드뷰 스크롤시 리스트뷰도 같이 스크롤됨
        gridview2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int id =  v.getId();			    // 이벤트 들어온 뷰의 아이값
                int action = event.getAction(); 	// 이벤트 동작(다운, 무브, 업 등.)

                // 터치 다운이벤트가 들어오고, 기존에 터치된 뷰가 없으면
                // 즉, 현재 이벤트가 들어온 뷰가 사용자가 직접 터치한 뷰이면
                if(action == MotionEvent.ACTION_DOWN && mTouchStartView == 0)
                    mTouchStartView = id;	// 뷰의 id값 저장.

                // 사용자가 터치한 뷰가 스크롤뷰 리스트뷰 (2번에 이벤트를 전달하기위해 구분)
                // 사용자가 직접 터치한 뷰이면 이벤트를 넘겨준다.
                // 사용자가 직접 터치 하지 않고 다른 뷰가 이벤트를 넘겨줬을 경우는 패스
                if(mTouchStartView == R.id.listView && mTouchStartView == id)
                    gridview2.dispatchTouchEvent(event);

                    // 그리드뷰이면 리스트뷰에 이벤트 넘겨줌
                else if(mTouchStartView == R.id.gridview3 && mTouchStartView == id)
                    listview.dispatchTouchEvent(event);

                // 터치가 끝나면 변수 값 초기화.
                // 플링시 그 이벤트도 같이 전달하기 위해서 마지막에 검사.
                // 플링은 무시하려면 위에 있는 터치 다운 이벤트 검사 바로 다음으로 옮기면 플링은 무시한다.
                if(action ==MotionEvent.ACTION_UP)
                    mTouchStartView = 0;

                return false;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            // 해당칸에 스케줄이 없을때, 클릭하고 플로팅 버튼을 클릭하면 액티비티 실행
            @Override
            public void onClick(View view) {
                if(go1 && go2) {
                    Intent intent = new Intent(getActivity(), schedule.class);
                    intent.putExtra("year", mParam1);
                    intent.putExtra("month", mParam2);
                    intent.putExtra("day", dayNumber);
                    intent.putExtra("hour",hour);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }
}
