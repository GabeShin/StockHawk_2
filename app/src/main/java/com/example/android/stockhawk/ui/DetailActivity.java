package com.example.android.stockhawk.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stockhawk.R;
import com.example.android.stockhawk.data.Contract;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Gabe on 2016-11-28.
 */
public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private String mSymbol;
    private Uri mContentUri;

    private final int DETAIL_LOADER = 100;

    @BindView(R.id.detail_name)
    TextView detailName;

    @BindView(R.id.detail_symbol)
    TextView detailSymbol;

    @BindView(R.id.detail_quote)
    TextView detailQuote;

    @BindView(R.id.detail_absolute)
    TextView detailAbsolute;

    @BindView(R.id.detail_percentage)
    TextView detailPercentage;

    @BindView(R.id.detail_graph)
    GraphView graphView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        mSymbol = this.getIntent().getExtras().getString("symbol");
        mContentUri = Contract.Quote.uri.buildUpon().appendPath(mSymbol).build();
    }

    @Override
    protected void onStart() {
        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mContentUri != null) {
            return new CursorLoader(
                    this,
                    mContentUri,
                    Contract.Quote.QUOTE_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            // Draw Graph
            String history = data.getString(data.getColumnIndexOrThrow(Contract.Quote.COLUMN_HISTORY));
            String[] array = history.split("-");
            ArrayList<Date> dates = new ArrayList<>();
            ArrayList<Double> quotes = new ArrayList<>();

            for (int i = array.length; i > 0; i--){

                Log.v("SIZE", "size is " + array.length);

                if (i % 2 == 1){
                    // odd
                    Date date = new Date(Long.valueOf(array[i - 1]));
                    dates.add(date);
                } else {
                    // even
                    Double quote = Double.valueOf(array[i - 1]);
                    quotes.add(quote);
                }
            }

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            for (int i = 0 ; i < dates.size(); i++){
                series.appendData(new DataPoint(dates.get(i), quotes.get(i)), true, dates.size());
            }

            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    String date = simpleDateFormat.format(dataPoint.getX());

                    String quote = String.format("%.2f", dataPoint.getY());

                    Toast.makeText(getApplication(), "On " + date + ": $" + quote, Toast.LENGTH_SHORT).show();
                }
            });

            graphView.getViewport().setScalable(true);
            graphView.getViewport().setScrollable(true);

            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setMinX(series.getLowestValueX());
            graphView.getViewport().setMaxX(series.getHighestValueX());

            graphView.addSeries(series);
            graphView.getGridLabelRenderer().setNumHorizontalLabels(5);
            graphView.getGridLabelRenderer().setVerticalLabelsVisible(true);
            graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));

            // Inflate the rest
            String symbol = data.getString(data.getColumnIndexOrThrow(Contract.Quote.COLUMN_SYMBOL));
            String name = data.getString(data.getColumnIndexOrThrow(Contract.Quote.COLUMN_NAME));
            String quote = data.getString(data.getColumnIndexOrThrow(Contract.Quote.COLUMN_PRICE));
            Float absolute = data.getFloat(data.getColumnIndexOrThrow(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
            Float percentage = data.getFloat(data.getColumnIndexOrThrow(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));

            detailName.setText(name);
            detailSymbol.setText(symbol);
            detailQuote.setText(getString(R.string.quote_dollar, quote));
            detailAbsolute.setText(getString(R.string.delta_absolute, String.valueOf(absolute)));
            detailPercentage.setText(getString(R.string.delta_percentage, String.valueOf(percentage)));

            if (absolute > 0 ){
                detailAbsolute.setTextColor(getResources().getColor(R.color.material_green_700));
                detailPercentage.setTextColor(getResources().getColor(R.color.material_green_700));
            } else {
                detailAbsolute.setTextColor(getResources().getColor(R.color.material_red_700));
                detailPercentage.setTextColor(getResources().getColor(R.color.material_red_700));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
