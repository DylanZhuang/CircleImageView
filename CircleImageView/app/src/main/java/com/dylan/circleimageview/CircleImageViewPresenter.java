package com.dylan.circleimageview;

/**
 * Description
 * author   Dylan.zhuang
 * Date:    16/7/7-下午2:51
 */
public class CircleImageViewPresenter {
    /**
     * 每种头像对应的旋转角度
     */
    private static final float[][] sRotationArray = { new float[] { 360 }, new float[] { 45, -135 },
            new float[] { 120, 0, -120 }, new float[] { 45, 135, -135, -45 },
            new float[] { 144, 72, 0, -72, -144 }};

    /**
     * 经过小圆圆心的两条直线ab，和经过大圆圆心的两条直线cd，a和c垂直，b和d垂直（对两个圆的条件不成立）
     * 公式为360/（n＊2），n代表小圆个数，分割策略如下
     * 基数圆经过每个圆的圆心画直线，1个圆除外；
     * 偶数圆经过每个圆的圆心画直线，并在公切线也画直线；
     * 分割策略提到的直线都经过大圆圆心
     */
    private static final int[] sAngleArray = {360, 90, 60, 45, 36};

    /**
     * 获取旋转角度
     * @param count
     * @return
     */
    public float[] getRotation(int count) {
        return count > 0 && count <= sRotationArray.length ? sRotationArray[count - 1] : null;
    }

    /**
     * 获取缩放比例
     * @param count
     * @return
     */
    public float getScale(int count) {
        int angle = getAngle(count);
        if (angle == 360) {
            return 1f;
        }
        double cot = getCot(angle);
        float scale = (float) (1f / (Math.sqrt(1 + Math.pow(cot, 2)) + 1));
        return scale;
    }

    /**
     * 获取最上面圆的左上角的值，方便计算每个圆的位置
     * @param radius
     * @param scale
     * @return
     */
    public float[] getTopPosition(float radius, float scale) {
        float x = radius * (1 - scale);
        float y = 0;
        return new float[] { x, y };
    }

    /**
     * 获取每个小头像应该平移的距离，找圆心
     * @param radius
     * @param rotation
     * @param scale
     * @param topX
     * @param topY
     * @return
     */
    public float[] getTranslatePosition(float radius, float rotation, float scale, float topX, float topY) {
        float smallRadius = radius * (1 - scale);
        double radian = Math.toRadians(rotation);
        double cos = Math.cos(radian);
        double sin = Math.sin(radian);
        float x = (float) (topX - smallRadius * sin);
        float y = (float) (topY + smallRadius * (1 - cos));
        return new float[] { x, y };
    }

    /**
     * 获取角度
     * @param count
     * @return
     */
    private int getAngle(int count) {
        return count > 0 && count <= sAngleArray.length ? sAngleArray[count - 1] : null;
    }

    /**
     * 获取cot值
     * @param angle
     * @return
     */
    private double getCot(int angle) {
        double radian = Math.toRadians(angle);
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        return  cos / sin;
    }
}
