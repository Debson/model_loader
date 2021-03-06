/* Date: 03/04/2019
 * Developer: Michal Debski
 * Github: github.com/debson
 * Class description:   Scene class is used to manage everything that is displayed on the screen. Class,
 *                      through few abstraction layers, provides necessary methods, that structures into
 *                      3 phases(Start, Run, Finishh) with additional functionality such as (OnFileDrop, OnWindowFocus etc.)
 *
 */

package com.michal.debski;

import com.michal.debski.environment.DirectionalLight;
import com.michal.debski.loader.Loader;
import com.michal.debski.utilities.Colour;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;


public class Scene implements GameHandlerInterface, SceneInterface
{
    private Shader shader;
    private Camera camera;
    private DirectionalLight dirLight;
    private float cameraMoveSpeed = 10.f;
    private Model myModel, floor, cube;

    private Gui gui;

    @Override
    public void OnWindowOpen()
    {
        // Initialize default shader
        shader = new Shader("shaders" + File.separator + "default.vert", "shaders" + File.separator + "default.frag");

        // Initialize scene camera
        camera = new Camera(new Vector2f(
                WindowProperties.getWidth(),
                WindowProperties.getHeight()),
                new Vector3f(0.f, 15.f, 30.f));


        // Use cross-platform compatible paths
        String path = "assets" + File.separator + "teapot.obj";
        String path2 = "assets" + File.separator + "nanosuit" + File.separator + "nanosuit.obj";
        String path3 = "assets" + File.separator + "teddybear.obj";
        String path4 = "assets" + File.separator + "head.obj";
        String path5 = "assets" + File.separator + "wolf.obj";

        myModel = new Model(path2);
        myModel.getTransform().setPosition(new Vector3f(0.f, 0.f, 0.f));
        myModel.getTransform().setScale(new Vector3f(1.f));
        //myModel.setColor(new Colour(1.f, 0.5f, 1.f));

        // Initialize and configure scene's floor
        floor = new Model("Floor", Loader.PrimitiveType.Plane);
        floor.setColor(new Colour(1.f, 0.f, 1.f, 1.f));

        // Initialize and configure primitive type - cube
        cube = new Model("Cube", Loader.PrimitiveType.Cube);
        cube.getTransform().setPosition(new Vector3f(0.f, 1.f, 10.f));

        // Initialize and configure directional light in the scene
        dirLight = new DirectionalLight(new Vector3f( -20.f, 30.f, -30.f), new Colour( 1.f));
        dirLight.getTransform().setScale(new Vector3f(3.f));

        // Initialize and create guy. It should be done after all objects in the scene were initialized.
        gui = new Gui();
        gui.createGui(Containers.panelContainer);
    }

    @Override
    public void OnWindowClose()
    {
        if(gui != null)
            gui.dispose();
    }

    @Override
    public void OnNewFrame()
    {

    }

    @Override
    public void OnFinishFrame()
    {

    }

    @Override
    public void OnRealtimeUpdate()
    {
        processCameraInput();

        if(gui != null)
        {
            gui.Update();
        }
    }

    @Override
    public void OnRealTimeRender()
    {
        // Set global shader to default shader
        ShaderManager.SetShader(shader);
        updateMatrices(shader);

        // Render scene to depth map
        dirLight.renderSceneWithShadows(this);
        // Render light box and set light uniforms
        dirLight.Render(camera.transform.getPosition());
        // Render scene as normal
        renderScene(shader);
    }

    @Override
    public void OnFileDrop(String pathOfDroppedFile)
    {
        myModel.createNew(pathOfDroppedFile);
        gui.replaceModel();
    }

    @Override
    public void OnWindowMove(int winX, int winY)
    {
        /*
         * If Gui class instance is initialized, update it's position
         */
        if(gui != null)
            gui.updatePosition();
    }

    @Override
    public void OnWindowFocus(boolean hasFocus)
    {
        // Small hack, so the gui window will always be visible only when game window is visible
        gui.setAlwaysOnTop(hasFocus);
    }

    private void updateMatrices(Shader shader)
    {
        /*
         *  Update shader matrices. View matrix should be updated every frame, but
         *  it's not necessary for a projection matrix.
         *
         */
        shader.use();
        shader.setMat4("projection", camera.getProjectionMatrix());
        shader.setMat4("view", camera.getViewMatrix());
    }

    private void processCameraInput()
    {
        Vector2f relMousePos = Input.GetRelativeMousePos();

        if(Input.IsKeyDown(Keycode.MouseMiddle) || Input.IsKeyDown(Keycode.E))
        {
            Window.CursorDisabled(true);
            camera.processMouseMovement(relMousePos.x, relMousePos.y);
        }
        else if(Input.IsKeyReleased(Keycode.MouseMiddle) || Input.IsKeyReleased(Keycode.E))
        {
            Window.CursorDisabled(false);
        }

        float speed = cameraMoveSpeed;

        if(Input.IsKeyDown(Keycode.LShift))
            speed *= 3.f;

        if(Input.IsKeyDown(Keycode.W))
            camera.processKeyboard(Camera.CameraMovement.Forward, (float)Time.deltaTime, speed);
        if(Input.IsKeyDown(Keycode.S))
            camera.processKeyboard(Camera.CameraMovement.Backward, (float)Time.deltaTime, speed);
        if(Input.IsKeyDown(Keycode.A))
            camera.processKeyboard(Camera.CameraMovement.Left, (float)Time.deltaTime, speed);
        if(Input.IsKeyDown(Keycode.D))
            camera.processKeyboard(Camera.CameraMovement.Right, (float)Time.deltaTime, speed);
    }

    @Override
    public void renderScene(Shader shader)
    {
        floor.Render();
        myModel.Render();
        cube.Render();
    }

    /*
     *   Main method creates an instance of our Scene class. Then another instance of a Game -
     *   class that will take the scene object as an argument. It is possible, because Scene class
     *   implements from GameInterfaceHandler(polymorphism). Then, Game methods are called, which
     *   through inheritance invokes Core class methods.
     *
     */
    public static void main(String[] args)
    {
        Scene scene = new Scene();
        Game game = new Game(scene);

        game.Open();
        game.Run();
        game.Close();
    }
}
