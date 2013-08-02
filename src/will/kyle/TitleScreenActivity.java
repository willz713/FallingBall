package will.kyle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class TitleScreenActivity extends Activity implements OnClickListener
{

	public static final String START_LEVEL_WITH_RESOURCE_ID = "932823";

	private static final String TAG = "TitleScreenActivity";
	private static final int REGULAR_LEVEL_DIALOG_ID = 1;
	private static final int INFINITE_LEVEL_DIALOG_ID = 2;

	private Button playRegularLevelButton;
	private Button playInfiniteLevelButton;

	private AlertDialog regularDialog;
	private AlertDialog infiniteDialog;

	protected CharSequence[] regularLevels = {"1: Tutorial(Easy)","2: No Walls, What!?","3: A Big Ball, No Wall",
			"4: Two Balls, No Wallz","5: Pink Land..., Serouisly?","6: Bouncy Ball!", "7: Reverse!"};	
	protected CharSequence[] infiniteLevels = {"1: Infinite, $50 If you win","2: Infinite 2 Balls!"};

	private SELECTION_MODE selectionMode;

	private DialogButtonClickHandler dialogButtonHandler;

	private enum SELECTION_MODE
	{
		REGULAR_LEVEL,INFINITE_LEVEL;
	}


	public enum REGULAR_LEVEL
	{
		Level_1(R.raw.level1),Level_2(R.raw.level2),Level_3(R.raw.level3),
		Level_4(R.raw.level4),Level_5(R.raw.level5),Level_6(R.raw.level6),Level_7(R.raw.level7);	

		private int resourceId;

		REGULAR_LEVEL(int id){
			this.resourceId = id;
		}

		public int getResourceId(){
			return resourceId;
		}

		public static REGULAR_LEVEL getLevelforInt(int levelId)
		{
			switch(levelId)
			{
			case 0:
				return REGULAR_LEVEL.Level_1;
			case 1:
				return REGULAR_LEVEL.Level_2;
			case 2:
				return REGULAR_LEVEL.Level_3;
			case 3:
				return REGULAR_LEVEL.Level_4;
			case 4:
				return REGULAR_LEVEL.Level_5;
			case 5:
				return REGULAR_LEVEL.Level_6;
			case 6:
				return REGULAR_LEVEL.Level_7;
			default:
				return REGULAR_LEVEL.Level_1;
			}
		}	
	}
	public enum INFINITE_LEVEL
	{
		infinite_1(R.raw.infinite1),infinite_2(R.raw.infinite2);		
		private int resourceId;

		INFINITE_LEVEL(int id)
		{
			this.resourceId = id;
		}

		public int getResourceId(){
			return resourceId;
		}

		public static INFINITE_LEVEL getLevelforInt(int levelId)
		{
			switch(levelId)
			{
			case 0:
				return INFINITE_LEVEL.infinite_1;
			case 1:
				return INFINITE_LEVEL.infinite_2;
			default:
				return INFINITE_LEVEL.infinite_2;
			}
		}	
	}

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.title_screen);
		playRegularLevelButton = (Button) findViewById(R.id.playLevelButton);
		playInfiniteLevelButton = (Button) findViewById(R.id.playInfiniteLevel);

		playRegularLevelButton.setOnClickListener(this);
		playInfiniteLevelButton.setOnClickListener(this);
	}


	public class DialogButtonClickHandler implements DialogInterface.OnClickListener
	{
		public void onClick( DialogInterface dialog, int clicked ) {
			Intent intent = new Intent(getApplicationContext(), MyFirstTriangleAndroid.class);
			startActivity(intent);
/*
			Intent intent = new Intent(getApplicationContext(), LevelActivity.class);
			if(selectionMode == SELECTION_MODE.REGULAR_LEVEL)
			{										
				REGULAR_LEVEL selectedLevel = REGULAR_LEVEL.getLevelforInt(clicked);
				Log.d(TAG,"Selected:" + selectedLevel.toString());						
				intent.putExtra(START_LEVEL_WITH_RESOURCE_ID,selectedLevel.getResourceId());
				startActivity(intent);
				regularDialog.dismiss();
			}
			else
			{
				INFINITE_LEVEL selectedLevel = INFINITE_LEVEL.getLevelforInt(clicked);
				Log.d(TAG,"Selected:" + selectedLevel.toString());						
				intent.putExtra(START_LEVEL_WITH_RESOURCE_ID,selectedLevel.getResourceId());
				startActivity(intent);	
				infiniteDialog.dismiss();
			}	
*/
		}
	}

	@Override
	protected Dialog onCreateDialog( int id ) 
	{
		if(dialogButtonHandler == null)
		{
			dialogButtonHandler = new DialogButtonClickHandler();
		}

		if(id == REGULAR_LEVEL_DIALOG_ID)
		{
			regularDialog = new AlertDialog.Builder( this )
			.setTitle( R.string.regular_levels )
			.setSingleChoiceItems( regularLevels,0,dialogButtonHandler )
			.create();

			return regularDialog;
		}
		else
		{
			infiniteDialog =  new AlertDialog.Builder( this )
			.setTitle( R.string.infinte_levels )
			.setSingleChoiceItems( infiniteLevels, 0,dialogButtonHandler )
			.create();			

			return infiniteDialog;
		}
	}

	@Override
	//get the dialog before it is displayed and the choice to 0
	protected void onPrepareDialog(int id, Dialog dialog) {

		ListView listView;

		if(id == REGULAR_LEVEL_DIALOG_ID)
		{
			listView = regularDialog.getListView();
			selectionMode = SELECTION_MODE.REGULAR_LEVEL;
		}
		else
		{
			listView = infiniteDialog.getListView();
			selectionMode = SELECTION_MODE.INFINITE_LEVEL;
		}
		if(listView != null)
		{
			listView.clearChoices(); 
		}
		super.onPrepareDialog(id, dialog);
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
		case R.id.playLevelButton:			
			showDialog( REGULAR_LEVEL_DIALOG_ID );
			break;
		case R.id.playInfiniteLevel:			
			showDialog( INFINITE_LEVEL_DIALOG_ID );
			break;
		}
	}


}
