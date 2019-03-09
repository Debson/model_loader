package com.michal.debski;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW.*;


import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Scene implements GameHandlerInterface
{
    private Shader shader;
    private Camera camera;
    private int vao, vbo;
    private Matrix4f model;
    private double mouseX, mouseY, mousePrevX, mousePrevY;


    @Override
    public void OnWindowOpen()
    {
        System.out.println("Opened!");

        shader = new Shader("shaders//default.vert", "shaders//default.frag");
        camera = new Camera(new Vector2f(
                WindowProperties.getWidth(),
                WindowProperties.getHeight()),
                new Vector3f(0.f, 0.f, 7.f));

        FloatBuffer verts = MemoryUtil.memAllocFloat(Vertices.cubeVertices.length);
        verts.put(Vertices.cubeVertices).flip();

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);
        memFree(verts);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void OnWindowClose()
    {

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
        Vector2f relMousePos = Input.GetRelativeMousePos();
        /*if(relMousePos.x != 0) System.out.println(relMousePos.x);
        if(relMousePos.y != 0) System.out.println(relMousePos.y);*/

        camera.processMouseMovement(relMousePos.x, relMousePos.y);

        if(Input.IsKeyPressed(Input.Keycode.E))
            System.out.println("EEEEEE");

    }

    @Override
    public void OnRealTimeRender()
    {
        model = new Matrix4f();

        updateMatrices();

        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, Vertices.cubeVertices.length);
        glBindVertexArray(0);
    }

    private void updateMatrices()
    {
        shader.use();
        shader.setMat4("projection", camera.getProjectionMatrix());
        shader.setMat4("view", camera.getViewMatrix());
        shader.setMat4("model", model);
    }

    public static void main(String[] args)
    {
        Scene scene = new Scene();
        Game game = new Game(scene);

        game.Open();
        game.Run();
        game.Close();
    }
}
