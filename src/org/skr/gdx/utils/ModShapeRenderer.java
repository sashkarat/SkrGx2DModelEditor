package org.skr.gdx.utils;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by rat on 27.07.14.
 */
public class ModShapeRenderer extends ShapeRenderer {

    private static final Vector2 f = new Vector2();
    private static final Vector2 v = new Vector2();
    private static final Vector2 lv = new Vector2();

    public void solidCircle( float x, float y, float r) {
        float angle = 0;
        float angleInc = 2 * (float)Math.PI / 20;
        for (int i = 0; i < 20; i++, angle += angleInc) {
            v.set((float)Math.cos(angle) * r + x, (float)Math.sin(angle) * r + y);
            if (i == 0) {
                lv.set(v);
                f.set(v);
                continue;
            }
            line(lv.x, lv.y, v.x, v.y);
            lv.set(v);
        }
        line(f.x, f.y, lv.x, lv.y);
    }
}
