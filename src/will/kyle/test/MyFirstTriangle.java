package will.kyle.test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class MyFirstTriangle implements ApplicationListener {
        private Mesh mesh;
        private ShapeRenderer sr;

    	private OrthographicCamera camera ;
    	private SpriteBatch batch;
    	Sprite sprite;
    	
            public void create () {
            	
//            	if (mesh == null) {
//                    mesh = new Mesh(true, 3, 3, 
//                            new VertexAttribute(Usage.Position, 3, "a_position"));          
//
//                    mesh.setVertices(new float[] { -0.5f, -0.5f, 0,
//                                                   0.5f, -0.5f, 0,
//                                                   0, 0.5f, 0 });   
//                    mesh.setIndices(new short[] { 0, 1, 2 });                       
//            	}
            	
            	
            	
            	Texture redBall = new Texture(Gdx.files.internal("ball_red.png"));
            	
            	
            	// TODO Auto-generated method stub
        		camera = new OrthographicCamera();
        		camera.setToOrtho(true);
        		
        		sr = new ShapeRenderer();
        		batch = new SpriteBatch();
        		
        		 
        		// setting a filter is optional, default = Nearest
        		 //texture.setFilter(Texture.!TextureFilter.Linear, Texture.!TextureFilter.Linear);
        		 		

        		// binding texture to sprite and setting some attributes
        		 sprite = new Sprite(redBall);
        		 
        		 
            }

            public void render () {
            	
            	Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        	    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//        	    mesh.render(GL10.GL_TRIANGLES, 0, 3);
        	    sr.setProjectionMatrix(camera.combined);
        	    sr.begin(ShapeType.Filled);
        	    sr.rect(100, 100, 100, 15);
        	    sr.end();
        	    
        		batch.setProjectionMatrix(camera.combined);
        		camera.update();
        		
        		
        		
        		batch.begin();
        		sprite.draw(batch);
        	
        		
        		
                batch.end();
            }
        
       
        @Override
        public void dispose() { }

        @Override
        public void pause() { }



        @Override
        public void resize(int width, int height) { }

        @Override
        public void resume() { }
}
