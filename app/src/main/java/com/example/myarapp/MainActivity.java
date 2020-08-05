package com.example.myarapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {
    private ModelRenderable modelRenderable;
    private Texture texture;
    private boolean isAdded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomArFragment fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        ModelRenderable.builder()
                .setSource(this,R.raw.fox_face)
                .build()
                .thenAccept(renderable ->{
                    modelRenderable = renderable;
                    modelRenderable.setShadowCaster(false);
                    modelRenderable.setShadowReceiver(false);

                });

        Texture.builder()
                .setSource(this,R.drawable.fox_face_mesh_texture)
                .build()
                .thenAccept(texture -> this.texture = texture );

        fragment.getArSceneView().setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            if(modelRenderable == null || texture == null)
                return;

            Frame frame = fragment.getArSceneView().getArFrame();
            Collection<AugmentedFace> augmentedFaces = frame.getUpdatedTrackables(AugmentedFace.class);
            for(AugmentedFace augmentedFace : augmentedFaces){
                if(isAdded)
                    return;
                AugmentedFaceNode augmentedFaceNode = new AugmentedFaceNode(augmentedFace);
                augmentedFaceNode.setParent(fragment.getArSceneView().getScene());
                augmentedFaceNode.setFaceRegionsRenderable(modelRenderable);
                augmentedFaceNode.setFaceMeshTexture(texture);
                isAdded = true;
            }
        });
    }
}