package will.kyle;

import will.kyle.test.MyFirstTriangle;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;

public class MyFirstTriangleAndroid extends AndroidApplication {
    @Override
        public void onCreate (Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                initialize(new MyFirstTriangle(), false);               
        }
}