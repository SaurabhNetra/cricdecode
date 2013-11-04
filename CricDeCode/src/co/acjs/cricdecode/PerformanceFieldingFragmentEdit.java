package co.acjs.cricdecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;


public class PerformanceFieldingFragmentEdit extends SherlockFragment{
	static PerformanceFieldingFragmentEdit	performanceFieldingFragmentEdit;
	EditText								slip_catches, close_catches, circle_catches, deep_catches, circle_runouts, circle_runouts_direct, deep_runouts, deep_runouts_direct, stumpings, byes, misfields, catches_dropped;
	TextView								my_team, opponent_team, venue, day, month, year, level, match_overs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		performanceFieldingFragmentEdit = this;
		View view = inflater.inflate(R.layout.performance_fielding_edit, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		init(view);
		((PerformanceFragmentEdit)getParentFragment()).viewInfo(PerformanceFragmentEdit.FIELDING);
	}

	public void init(View view){
		day = (TextView)view.findViewById(R.id.day);
		month = (TextView)view.findViewById(R.id.month);
		year = (TextView)view.findViewById(R.id.year);
		my_team = (TextView)view.findViewById(R.id.my_team);
		opponent_team = (TextView)view.findViewById(R.id.opponent_team);
		venue = (TextView)view.findViewById(R.id.venue);
		level = (TextView)view.findViewById(R.id.level);
		match_overs = (TextView)view.findViewById(R.id.overs);
		slip_catches = (EditText)view.findViewById(R.id.slip_catches);
		slip_catches.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(slip_catches.getText().toString().equals("0")){
						slip_catches.setText("");
					}
				}else{
					if(slip_catches.getText().toString().equals("")){
						slip_catches.setText("0");
					}
				}
			}
		});
		close_catches = (EditText)view.findViewById(R.id.close_catches);
		close_catches.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(close_catches.getText().toString().equals("0")){
						close_catches.setText("");
					}
				}else{
					if(close_catches.getText().toString().equals("")){
						close_catches.setText("0");
					}
				}
			}
		});
		circle_catches = (EditText)view.findViewById(R.id.circle_catches);
		circle_catches.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(circle_catches.getText().toString().equals("0")){
						circle_catches.setText("");
					}
				}else{
					if(circle_catches.getText().toString().equals("")){
						circle_catches.setText("0");
					}
				}
			}
		});
		deep_catches = (EditText)view.findViewById(R.id.deep_catches);
		deep_catches.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(deep_catches.getText().toString().equals("0")){
						deep_catches.setText("");
					}
				}else{
					if(deep_catches.getText().toString().equals("")){
						deep_catches.setText("0");
					}
				}
			}
		});
		circle_runouts = (EditText)view.findViewById(R.id.circle_runouts);
		circle_runouts.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(circle_runouts.getText().toString().equals("0")){
						circle_runouts.setText("");
					}
				}else{
					if(circle_runouts.getText().toString().equals("")){
						circle_runouts.setText("0");
					}
				}
			}
		});
		circle_runouts_direct = (EditText)view.findViewById(R.id.circle_runouts_direct);
		circle_runouts_direct.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(circle_runouts_direct.getText().toString().equals("0")){
						circle_runouts_direct.setText("");
					}
				}else{
					if(circle_runouts_direct.getText().toString().equals("")){
						circle_runouts_direct.setText("0");
					}
				}
			}
		});
		deep_runouts = (EditText)view.findViewById(R.id.deep_runouts);
		deep_runouts.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(deep_runouts.getText().toString().equals("0")){
						deep_runouts.setText("");
					}
				}else{
					if(deep_runouts.getText().toString().equals("")){
						deep_runouts.setText("0");
					}
				}
			}
		});
		deep_runouts_direct = (EditText)view.findViewById(R.id.deep_runouts_direct);
		deep_runouts_direct.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(deep_runouts_direct.getText().toString().equals("0")){
						deep_runouts_direct.setText("");
					}
				}else{
					if(deep_runouts_direct.getText().toString().equals("")){
						deep_runouts_direct.setText("0");
					}
				}
			}
		});
		stumpings = (EditText)view.findViewById(R.id.stumpings);
		stumpings.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(stumpings.getText().toString().equals("0")){
						stumpings.setText("");
					}
				}else{
					if(stumpings.getText().toString().equals("")){
						stumpings.setText("0");
					}
				}
			}
		});
		byes = (EditText)view.findViewById(R.id.byes);
		byes.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(byes.getText().toString().equals("0")){
						byes.setText("");
					}
				}else{
					if(byes.getText().toString().equals("")){
						byes.setText("0");
					}
				}
			}
		});
		misfields = (EditText)view.findViewById(R.id.misfields);
		misfields.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(misfields.getText().toString().equals("0")){
						misfields.setText("");
					}
				}else{
					if(misfields.getText().toString().equals("")){
						misfields.setText("0");
					}
				}
			}
		});
		catches_dropped = (EditText)view.findViewById(R.id.catches_dropped_fielding);
		catches_dropped.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				if(hasFocus){
					if(catches_dropped.getText().toString().equals("0")){
						catches_dropped.setText("");
					}
				}else{
					if(catches_dropped.getText().toString().equals("")){
						catches_dropped.setText("0");
					}
				}
			}
		});
	}
}
