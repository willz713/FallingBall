package will.kyle.test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class MyFirstTriangle implements ApplicationListener {
        private Mesh mesh;

        @Override
        public void create() {
                if (mesh == null) {
                        mesh = new Mesh(true, 3, 3, 
                                new VertexAttribute(Usage.Position, 3, "a_position"));          

                        mesh.setVertices(new float[] { -0.5f, -0.5f, 0,
                                                       0.5f, -0.5f, 0,
                                                       0, 0.5f, 0 });   
                        mesh.setIndices(new short[] { 0, 1, 2 });                       
                }
        }

        @Override
        public void dispose() { }

        @Override
        public void pause() { }

        @Override
        public void render() {
                Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
                mesh.render(GL10.GL_TRIANGLES, 0, 3);
        }

        @Override
        public void resize(int width, int height) { }

        @Override
        public void resume() { }
}
