import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WigglePaint: ベクトルアプローチによる描画ソフトのプロトタイプ
 * このファイル一つでコンパイル・実行が可能です。
 * 機能追加：曲線描画、デバッグモード、UI改善
 */
public class WigglePaint {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Wiggle Paint Prototype");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            DrawingCanvas canvas = new DrawingCanvas();
            frame.add(canvas, BorderLayout.CENTER);

            // --- メニューバーの作成 ---
            JMenuBar menuBar = new JMenuBar();

            // --- オプションメニュー ---
            JMenu optionMenu = new JMenu("オプション");
            menuBar.add(optionMenu);

            // --- ツール選択メニュー ---
            JMenu toolMenu = new JMenu("ツール");
            ButtonGroup toolGroup = new ButtonGroup();
            JRadioButtonMenuItem pencilItem = new JRadioButtonMenuItem("鉛筆 (Pencil)", true);
            JRadioButtonMenuItem lineItem = new JRadioButtonMenuItem("線 (Line)");
            JRadioButtonMenuItem eraserItem = new JRadioButtonMenuItem("消しゴム (Eraser)");
            pencilItem.addActionListener(e -> canvas.setTool(DrawingCanvas.Tool.PENCIL));
            lineItem.addActionListener(e -> canvas.setTool(DrawingCanvas.Tool.LINE));
            eraserItem.addActionListener(e -> canvas.setTool(DrawingCanvas.Tool.ERASER));
            toolGroup.add(pencilItem);
            toolGroup.add(lineItem);
            toolGroup.add(eraserItem);
            toolMenu.add(pencilItem);
            toolMenu.add(lineItem);
            toolMenu.add(eraserItem);
            optionMenu.add(toolMenu);
            optionMenu.addSeparator();

            // --- ペンの太さ選択メニュー ---
            JMenu penThicknessMenu = new JMenu("ペンの太さ");
            ButtonGroup thicknessGroup = new ButtonGroup();
            JRadioButtonMenuItem smallItem = new JRadioButtonMenuItem("Small", true);
            JRadioButtonMenuItem normalItem = new JRadioButtonMenuItem("Normal");
            JRadioButtonMenuItem bigItem = new JRadioButtonMenuItem("Big");
            smallItem.addActionListener(e -> canvas.setPenThickness(2f));
            normalItem.addActionListener(e -> canvas.setPenThickness(4f));
            bigItem.addActionListener(e -> canvas.setPenThickness(6f));
            thicknessGroup.add(smallItem);
            thicknessGroup.add(normalItem);
            thicknessGroup.add(bigItem);
            penThicknessMenu.add(smallItem);
            penThicknessMenu.add(normalItem);
            penThicknessMenu.add(bigItem);
            optionMenu.add(penThicknessMenu);
            optionMenu.addSeparator();

            // --- 線のつなぎ方メニュー ---
            JMenu lineStyleMenu = new JMenu("線のつなぎ方");
            ButtonGroup lineStyleGroup = new ButtonGroup();
            JRadioButtonMenuItem straightLineItem = new JRadioButtonMenuItem("直線", true);
            JRadioButtonMenuItem quadCurveItem = new JRadioButtonMenuItem("2次スプライン曲線");
            JRadioButtonMenuItem bezierCurveItem = new JRadioButtonMenuItem("ベジェ曲線");
            straightLineItem.addActionListener(e -> canvas.setLineStyle(DrawingCanvas.LineStyle.STRAIGHT));
            quadCurveItem.addActionListener(e -> canvas.setLineStyle(DrawingCanvas.LineStyle.QUAD_CURVE));
            bezierCurveItem.addActionListener(e -> canvas.setLineStyle(DrawingCanvas.LineStyle.BEZIER_CURVE));
            lineStyleGroup.add(straightLineItem);
            lineStyleGroup.add(quadCurveItem);
            lineStyleGroup.add(bezierCurveItem);
            lineStyleMenu.add(straightLineItem);
            lineStyleMenu.add(quadCurveItem);
            lineStyleMenu.add(bezierCurveItem);
            optionMenu.add(lineStyleMenu);

            // --- 表示メニュー ---
            JMenu viewMenu = new JMenu("表示");
            menuBar.add(viewMenu);

            // --- フレームレート選択メニュー (表示メニューへ移動) ---
            JMenu frameRateMenu = new JMenu("フレームレート");
            ButtonGroup frameRateGroup = new ButtonGroup();
            JRadioButtonMenuItem slowRateItem = new JRadioButtonMenuItem("Slow (120ms)");
            JRadioButtonMenuItem normalRateItem = new JRadioButtonMenuItem("Normal (80ms)", true);
            JRadioButtonMenuItem fastRateItem = new JRadioButtonMenuItem("Fast (40ms)");
            slowRateItem.addActionListener(e -> canvas.setFrameRate(120));
            normalRateItem.addActionListener(e -> canvas.setFrameRate(80));
            fastRateItem.addActionListener(e -> canvas.setFrameRate(40));
            frameRateGroup.add(slowRateItem);
            frameRateGroup.add(normalRateItem);
            frameRateGroup.add(fastRateItem);
            frameRateMenu.add(slowRateItem);
            frameRateMenu.add(normalRateItem);
            frameRateMenu.add(fastRateItem);
            viewMenu.add(frameRateMenu);
            viewMenu.addSeparator();

            // --- アンチエイリアシング選択メニュー ---
            JMenu antiAliasMenu = new JMenu("アンチエイリアシング");
            ButtonGroup antiAliasGroup = new ButtonGroup();
            JRadioButtonMenuItem antiAliasOn = new JRadioButtonMenuItem("On", true);
            JRadioButtonMenuItem antiAliasOff = new JRadioButtonMenuItem("Off");
            antiAliasOn.addActionListener(e -> canvas.setAntialiasing(true));
            antiAliasOff.addActionListener(e -> canvas.setAntialiasing(false));
            antiAliasGroup.add(antiAliasOn);
            antiAliasGroup.add(antiAliasOff);
            antiAliasMenu.add(antiAliasOn);
            antiAliasMenu.add(antiAliasOff);
            viewMenu.add(antiAliasMenu);
            viewMenu.addSeparator();
            
            // --- デバッグモード選択 ---
            JCheckBoxMenuItem debugModeItem = new JCheckBoxMenuItem("デバッグモード");
            debugModeItem.addActionListener(e -> canvas.setDebugMode(debugModeItem.isSelected()));
            viewMenu.add(debugModeItem);

            frame.setJMenuBar(menuBar);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class DrawingCanvas extends JPanel {

    enum Tool { PENCIL, ERASER, LINE }
    enum LineStyle { STRAIGHT, QUAD_CURVE, BEZIER_CURVE }

    private Tool currentTool = Tool.PENCIL;
    private LineStyle currentLineStyle = LineStyle.STRAIGHT;
    private final List<WigglePath> paths = new CopyOnWriteArrayList<>();
    private WigglePath currentPath;
    private Point mousePreviewPosition;

    private int currentFrame = 0;
    private int currentDelay = 80;
    private final double WIGGLE_AMOUNT = 1.0;
    private final double MIN_DISTANCE = 2.5;

    private Timer animationTimer;
    private Stroke currentStroke;
    private boolean isAntialiasingOn = true;
    private boolean isDebugMode = false;
    
    private BufferedImage debugImage;
    private double debugImageAngle = 0;

    public DrawingCanvas() {
        this.setBackground(Color.WHITE);
        setPenThickness(2f);

        try {
            URL imageUrl = getClass().getResource("doudou.png");
            if (imageUrl != null) {
                debugImage = ImageIO.read(imageUrl);
            } else {
                System.err.println("デバッグ画像 'doudou.png' が見つかりません。");
            }
        } catch (IOException e) {
            System.err.println("デバッグ画像の読み込みに失敗しました: " + e.getMessage());
        }

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (currentTool == Tool.PENCIL) {
                    currentPath = new WigglePath();
                    currentPath.addPoint(e.getX(), e.getY(), WIGGLE_AMOUNT);
                    paths.add(currentPath);
                } else if (currentTool == Tool.LINE) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (currentPath == null) {
                            currentPath = new WigglePath();
                            paths.add(currentPath);
                        }
                        currentPath.addPoint(e.getX(), e.getY(), WIGGLE_AMOUNT);
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        currentPath = null;
                        mousePreviewPosition = null;
                    }
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentTool == Tool.PENCIL && currentPath != null) {
                    Point2D.Double lastPoint = currentPath.getLastMasterPoint();
                    if (lastPoint != null && e.getPoint().distance(lastPoint) > MIN_DISTANCE) {
                        currentPath.addPoint(e.getX(), e.getY(), WIGGLE_AMOUNT);
                    }
                } else if (currentTool == Tool.ERASER) {
                    erasePathsNear(e.getPoint(), 20);
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentTool == Tool.PENCIL) {
                    currentPath = null;
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (currentTool == Tool.LINE && currentPath != null) {
                    mousePreviewPosition = e.getPoint();
                    repaint();
                }
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        animationTimer = new Timer(currentDelay, e -> {
            currentFrame = (currentFrame + 1) % 3;
            if(isDebugMode && debugImage != null) {
                // フレームレートが速いほど回転も速くする
                debugImageAngle += (80.0 / currentDelay) * 0.05;
            }
            repaint();
        });
        animationTimer.start();
    }

    public void setTool(Tool tool) {
        this.currentTool = tool;
        currentPath = null;
        mousePreviewPosition = null;
        repaint();
    }

    public void setLineStyle(LineStyle style) {
        this.currentLineStyle = style;
        clearCanvas();
    }

    public void setFrameRate(int delay) {
        this.currentDelay = delay;
        animationTimer.setDelay(delay);
    }

    public void setPenThickness(float thickness) {
        this.currentStroke = new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    public void setAntialiasing(boolean isOn) {
        this.isAntialiasingOn = isOn;
        repaint();
    }
    
    public void setDebugMode(boolean isDebug) {
        this.isDebugMode = isDebug;
        repaint();
    }

    private void clearCanvas() {
        paths.clear();
        currentPath = null;
        mousePreviewPosition = null;
        repaint();
    }

    private void erasePathsNear(Point p, double radius) {
        paths.removeIf(path -> path.isNear(p, radius));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (isAntialiasingOn) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        g2d.setStroke(currentStroke);
        g2d.setColor(Color.BLACK);

        for (WigglePath path : paths) {
            path.draw(g2d, currentFrame, currentLineStyle, isDebugMode, currentStroke);
        }

        if (currentTool == Tool.LINE && currentPath != null && currentPath.hasPoints() && mousePreviewPosition != null) {
            Point2D.Double lastPoint = currentPath.getLastMasterPoint();
            if (lastPoint != null) {
                g2d.setStroke(currentStroke); // 通常のストロークを使用
                g2d.setColor(Color.BLACK);   // 通常の色を使用
                g2d.drawLine((int) lastPoint.x, (int) lastPoint.y, mousePreviewPosition.x, mousePreviewPosition.y);
            }
        }
        
        if(isDebugMode) {
            // デバッグ情報を右上に表示
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
            g2d.drawString("Delay: " + currentDelay + "ms", getWidth() - 120, 20);
            g2d.drawString("Frame: " + currentFrame, getWidth() - 120, 40);

            // デバッグ画像を右下に回転させて表示
            if(debugImage != null) {
                int imgX = getWidth() - debugImage.getWidth() - 20;
                int imgY = getHeight() - debugImage.getHeight() - 20;
                // 保存したグラフィックスの状態を復元するためにアフィン変換を保存
                AffineTransform old = g2d.getTransform();
                g2d.rotate(debugImageAngle, imgX + debugImage.getWidth() / 2.0, imgY + debugImage.getHeight() / 2.0);
                g2d.drawImage(debugImage, imgX, imgY, null);
                // グラフィックスの状態を元に戻す
                g2d.setTransform(old);
            }
        }
    }
}

class WigglePath {
    private final List<WigglePoint> points = new ArrayList<>();

    public void addPoint(double x, double y, double wiggleAmount) {
        points.add(new WigglePoint(x, y, wiggleAmount));
    }

    public Point2D.Double getLastMasterPoint() {
        if (points.isEmpty()) return null;
        return points.get(points.size() - 1).masterPoint;
    }

    public boolean hasPoints() {
        return !points.isEmpty();
    }

    public boolean isNear(Point p, double radius) {
        for (WigglePoint point : points) {
            if (p.distance(point.masterPoint) < radius) return true;
        }
        return false;
    }

    public void draw(Graphics2D g2d, int frameIndex, DrawingCanvas.LineStyle style, boolean isDebugMode, Stroke stroke) {
        if (points.size() < 2) return;

        g2d.setStroke(stroke);
        g2d.setColor(isDebugMode ? Color.RED : Color.BLACK);

        switch (style) {
            case STRAIGHT:
                drawStraight(g2d, frameIndex);
                break;
            case QUAD_CURVE:
                drawQuadCurve(g2d, frameIndex);
                break;
            case BEZIER_CURVE:
                drawBezierCurve(g2d, frameIndex);
                break;
        }
        
        if(isDebugMode) {
            g2d.setColor(Color.BLUE);
            for(WigglePoint p : points) {
                Point2D.Double master = p.masterPoint;
                g2d.fillOval((int)master.x - 3, (int)master.y - 3, 6, 6);
            }
        }
    }

    private void drawStraight(Graphics2D g2d, int frameIndex) {
        for (int i = 0; i < points.size() - 1; i++) {
            Point2D.Double p1 = points.get(i).getFramePoint(frameIndex);
            Point2D.Double p2 = points.get(i + 1).getFramePoint(frameIndex);
            g2d.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
        }
    }

    private void drawQuadCurve(Graphics2D g2d, int frameIndex) {
        if (points.size() < 3) {
            drawStraight(g2d, frameIndex); // 点が少ない場合は直線で描画
            return;
        }
        
        List<Point2D.Double> framePoints = new ArrayList<>();
        for(WigglePoint p : points) framePoints.add(p.getFramePoint(frameIndex));

        for (int i = 0; i < framePoints.size() - 2; i++) {
            Point2D.Double p1 = framePoints.get(i);
            Point2D.Double p2 = framePoints.get(i + 1);
            Point2D.Double p3 = framePoints.get(i + 2);

            Point2D.Double m1 = new Point2D.Double((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
            Point2D.Double m2 = new Point2D.Double((p2.x + p3.x) / 2, (p2.y + p3.y) / 2);

            if (i == 0) {
                 g2d.draw(new QuadCurve2D.Double(p1.x, p1.y, p2.x, p2.y, m2.x, m2.y));
            } else {
                 g2d.draw(new QuadCurve2D.Double(m1.x, m1.y, p2.x, p2.y, m2.x, m2.y));
            }
        }
    }

    private void drawBezierCurve(Graphics2D g2d, int frameIndex) {
        if (points.size() < 2) return;
        if (points.size() < 3) {
            drawStraight(g2d, frameIndex);
            return;
        }
        
        List<Point2D.Double> framePoints = new ArrayList<>();
        for(WigglePoint p : points) framePoints.add(p.getFramePoint(frameIndex));

        for (int i = 0; i < framePoints.size() - 1; i++) {
            Point2D.Double p0 = (i > 0) ? framePoints.get(i - 1) : framePoints.get(i);
            Point2D.Double p1 = framePoints.get(i);
            Point2D.Double p2 = framePoints.get(i + 1);
            Point2D.Double p3 = (i < framePoints.size() - 2) ? framePoints.get(i + 2) : p2;

            double c1x = p1.x + (p2.x - p0.x) / 6.0;
            double c1y = p1.y + (p2.y - p0.y) / 6.0;
            double c2x = p2.x - (p3.x - p1.x) / 6.0;
            double c2y = p2.y - (p3.y - p1.y) / 6.0;
            
            g2d.draw(new CubicCurve2D.Double(p1.x, p1.y, c1x, c1y, c2x, c2y, p2.x, p2.y));
        }
    }
}

class WigglePoint {
    final Point2D.Double masterPoint;
    private final Point2D.Double[] offsetVectors = new Point2D.Double[3];
    private static final double MIN_WIGGLE_DISTANCE = 0.1;

    public WigglePoint(double x, double y, double wiggleAmount) {
        this.masterPoint = new Point2D.Double(x, y);
        generateOffsetVectors(wiggleAmount);
    }

    private void generateOffsetVectors(double amount) {
        for (int i = 0; i < 3; i++) {
            double dx, dy;
            do {
                dx = (Math.random() - 0.5) * amount * 2;
                dy = (Math.random() - 0.5) * amount * 2;
            } while (Math.hypot(dx, dy) < MIN_WIGGLE_DISTANCE);
            offsetVectors[i] = new Point2D.Double(dx, dy);
        }
    }

    public Point2D.Double getFramePoint(int frameIndex) {
        Point2D.Double offset = offsetVectors[frameIndex];
        return new Point2D.Double(masterPoint.x + offset.x, masterPoint.y + offset.y);
    }
}
