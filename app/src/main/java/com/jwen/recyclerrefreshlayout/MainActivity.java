package com.jwen.recyclerrefreshlayout;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jwen.recyclerrefreshlayout.widget.OnLoadingListener;
import com.jwen.recyclerrefreshlayout.widget.OnRefreshListener;
import com.jwen.recyclerrefreshlayout.widget.RefreshLayout;

public class MainActivity extends AppCompatActivity {

    private Handler mHandler = new Handler(){};
    RefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setContentView(R.layout.activity_main2);
//        setContentView(R.layout.activity_main3);

        refreshLayout = (RefreshLayout) findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.stopRefresh();
                    }
                },2000);
            }
        });

        refreshLayout.setOnLoadingListener(new OnLoadingListener() {
            @Override
            public void onLoading() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.stopLoading();
                    }
                },2000);
            }
        });

        initRecyclerView();
//        initListView();
//        initScrollView();

    }

    private void initScrollView() {

    }

    private void initListView() {
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ListViewAdapter());
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RecyclerViewAdapter());
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_refresh,null);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.mTextView.setText("RecyclerRefreshLayout" + position);
        }
        @Override
        public int getItemCount() {
            return 20;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;
            public ViewHolder(View view){
                super(view);
                mTextView = (TextView) view.findViewById(R.id.tv_refresh);
            }
        }
    }

    public class ListViewAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 20;
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_refresh,null);

            ((TextView)view.findViewById(R.id.tv_refresh)).setText("RecyclerRefreshLayout" + position);
            return view;
        }
    }



}
