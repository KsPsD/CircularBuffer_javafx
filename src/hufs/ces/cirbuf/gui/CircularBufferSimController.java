package hufs.ces.cirbuf.gui;

import java.io.IOException;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;
import hufs.ces.cirbuf.CircularBuffer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CircularBufferSimController extends AnchorPane {

	private final static int DEFAULT_BUFFER_COUNT = 10;

	public Stage parentStage = null;

	volatile CircularBuffer<String> cirbuf1 = null;
	volatile CircularBuffer<String> cirbuf2 = null;
	volatile CircularBuffer<String> cirbuf3 = null;
	volatile Iterator<BigInteger> fibGen = null;
	BufferShape[] bufShapes1 = null;
	BufferShape[] bufShapes2 = null;
	BufferShape[] bufShapes3 = null;
	Queue<String> short_queue = new LinkedList<>();
	int count = 0;
	// volatile IntegerProperty bufUseCount = new SimpleIntegerProperty(0);

	@FXML
	private AnchorPane root;

	@FXML
	private TextField tfBufSize;

	@FXML
	private Label lblNumber;

	@FXML
	private Label lblFiboP;

	@FXML
	private Label lblFiboC;

	@FXML
	private Label lblCount1;

	@FXML
	private Label lblCount2;

	@FXML
	private Label lblCount3;

	@FXML
	private Button btnStart;

	@FXML
	private Pane drawPane;

	@FXML
	void handleBtnStart(ActionEvent event) {
		if (cirbuf1 == null || cirbuf2 == null || cirbuf3 == null) {
			initCircularBuffer(DEFAULT_BUFFER_COUNT);
		}
		Thread prod1 = new Thread(new Producer1Task());
		Thread cons1 = new Thread(new Consumer1Task());
		Thread cons2 = new Thread(new Consumer2Task());
		Thread cons3 = new Thread(new Consumer3Task());
		prod1.start();
		cons1.start();
		cons2.start();
		cons3.start();

	}

	@FXML
	void handleBufSizeIn(ActionEvent event) {
		int siz = Integer.parseInt(tfBufSize.getText());
		initCircularBuffer(siz);
	}

	public CircularBufferSimController() {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cirbuf.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		initialize();
	}

	private void initialize() {
		fibGen = new BigFibonacciIterator();
	}

	void initCircularBuffer(int siz) {
		// bufUseCount = new SimpleIntegerProperty(0);
		cirbuf1 = new CircularBuffer<String>(siz);
		cirbuf2 = new CircularBuffer<String>(siz);
		cirbuf3 = new CircularBuffer<String>(siz);
		bufShapes1 = new BufferShape[cirbuf1.getBufSize()];
		bufShapes2 = new BufferShape[cirbuf2.getBufSize()];
		bufShapes3 = new BufferShape[cirbuf3.getBufSize()];
		buildCircularBufferShape(cirbuf1, bufShapes1);
		buildCircularBufferShape(cirbuf2, bufShapes2);
		buildCircularBufferShape(cirbuf3, bufShapes3);

	}

	void buildCircularBufferShape(CircularBuffer<String> cirbuf, BufferShape[] bufShapes) {
//		drawPane.getChildren().clear();

		if (cirbuf == cirbuf1) {
			double cx = drawPane.getWidth() / 7;
			double cy = drawPane.getHeight() / 8;
			// System.out.println("cx="+cx+" cy="+cy);

			double outrad = cx * 0.6;

			int siz = cirbuf.getBufSize();
			double angl = 2 * Math.PI / siz;

			for (int i = 0; i < siz; ++i) {
				bufShapes[i] = new BufferShape();
				bufShapes[i].setBufPath(cx, cy, outrad, angl);
				bufShapes[i].setText(String.valueOf(i), cx, cy, outrad, angl);
				bufShapes[i].setRot(i * Math.toDegrees(angl), cx, cy);
				bufShapes[i].setBackground(Color.SNOW);
				drawPane.getChildren().add(bufShapes[i]);
			}

		} else if (cirbuf == cirbuf2) {
			double cx = drawPane.getWidth() / 7;
			double cy = drawPane.getHeight() / 1.5;
			// System.out.println("cx="+cx+" cy="+cy);

			double outrad = cx * 0.6;

			int siz = cirbuf.getBufSize();
			double angl = 2 * Math.PI / siz;

			for (int i = 0; i < siz; ++i) {
				bufShapes[i] = new BufferShape();
				bufShapes[i].setBufPath(cx, cy, outrad, angl);
				bufShapes[i].setText(String.valueOf(i), cx, cy, outrad, angl);
				bufShapes[i].setRot(i * Math.toDegrees(angl), cx, cy);
				bufShapes[i].setBackground(Color.SNOW);
				drawPane.getChildren().add(bufShapes[i]);
			}

		} else if (cirbuf == cirbuf3) {

			double cx = drawPane.getWidth() / 1.5;
			double cy = drawPane.getHeight() / 2.5;
			// System.out.println("cx="+cx+" cy="+cy);

			double outrad = cx * 0.3;

			int siz = cirbuf.getBufSize();
			double angl = 2 * Math.PI / siz;

			for (int i = 0; i < siz; ++i) {
				bufShapes[i] = new BufferShape();
				bufShapes[i].setBufPath(cx, cy, outrad, angl);
				bufShapes[i].setText(String.valueOf(i), cx, cy, outrad, angl);
				bufShapes[i].setRot(i * Math.toDegrees(angl), cx, cy);
				bufShapes[i].setBackground(Color.SNOW);
				drawPane.getChildren().add(bufShapes[i]);
			}
		}

	}

	void setBufferShapeColor(CircularBuffer<String> cirbuf, BufferShape[] bufShapes) {
		int siz = cirbuf.getBufSize();
		int front = cirbuf.getFront();
		int rear = cirbuf.getRear();
		for (int i = 0; i < siz; ++i) {
			bufShapes[i].setBackground(Color.SNOW);
		}
		int bp = front;
		for (int count = 1; count <= cirbuf.getOccupiedBufferCount(); ++count) {
			bufShapes[bp].setBackground(Color.CYAN);
			bp = (bp + 1) % siz;
		}
		if (front != rear) {
			bufShapes[front].setBackground(Color.GREEN);
			bufShapes[rear].setBackground(Color.BLUE);
		} else if (cirbuf.getOccupiedBufferCount() > 0) {
			bufShapes[front].setBackground(Color.GREEN);
		} else {
			bufShapes[front].setBackground(Color.CYAN);
		}

	}

	private class Producer1Task implements Runnable {
		public void run() {
			try {
				while (true) {

					short_queue.offer(String.valueOf(fibGen.next()));
					String peekV = short_queue.peek();

					if (count % 2 == 0) {

						cirbuf1.write(short_queue.poll());
						count++;
					} else if (count % 2 == 1) {

						cirbuf2.write(short_queue.poll());
						count++;
					}
//					System.out.println("Producer writes " + i);
//					cirbuf.write(String.valueOf(fibGen.next())); // Add a value to the buffer
					Platform.runLater(() -> {
						lblFiboP.setText(peekV);
						setBufferShapeColor(cirbuf1, bufShapes1);
						lblCount1.setText(String.valueOf(cirbuf1.getOccupiedBufferCount()));
						double ratio1 = (double) cirbuf1.getOccupiedBufferCount() / cirbuf1.getBufSize();
						Utils.setBackground(lblCount1, Utils.getRatioColor(ratio1));
						setBufferShapeColor(cirbuf2, bufShapes2);
						lblCount2.setText(String.valueOf(cirbuf2.getOccupiedBufferCount()));
						double ratio2 = (double) cirbuf2.getOccupiedBufferCount() / cirbuf2.getBufSize();
						Utils.setBackground(lblCount2, Utils.getRatioColor(ratio2));
						// System.out.println("lblCount Style = "+lblCount.getStyle());
					});
					// Put the thread into sleep
//					Thread.sleep((int)(Math.random() * 1000));
					Thread.sleep((int) (Math.random() * 250)); // 1초에 4개
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	// prod1을 그냥 큐로 만들기만하고 con1,2 가 원래 prod코드처럼 write하고 consume까지??

	// A task for reading and deleting an int from the buffer
	private class Consumer1Task implements Runnable {
		public void run() {
			try {
				while (true) {
					String sval = cirbuf1.read();// 이 부근 에서 그냥 두개 서큐러 버퍼 중에서 1개 읽으면 될듯

					System.out.println("\t\t\tConsumer1 reads " + sval);
					Platform.runLater(() -> {
						lblNumber.setText("Consumer1");
						lblFiboC.setText(sval);
						setBufferShapeColor(cirbuf1, bufShapes1);
						lblCount1.setText(String.valueOf(cirbuf1.getOccupiedBufferCount()));
						double ratio = (double) cirbuf1.getOccupiedBufferCount() / cirbuf1.getBufSize();
						Utils.setBackground(lblCount1, Utils.getRatioColor(ratio));
						// System.out.println("lblCount Style = "+lblCount.getStyle());
						cirbuf3.write(sval);
					});
					// Put the thread into sleep
//					Thread.sleep((int)(Math.random() * 1000));
					Thread.sleep((int) (Math.random() * 500)); // 1초에 4개

				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class Consumer2Task implements Runnable {
		public void run() {
			try {
				while (true) {
					String sval = cirbuf2.read();// 이 부근 에서 그냥 두개 서큐러 버퍼 중에서 1개 읽으면 될듯

					System.out.println("\t\t\tConsumer2 reads " + sval);
					Platform.runLater(() -> {
						lblNumber.setText("Consumer2");
						lblFiboC.setText(sval);
						setBufferShapeColor(cirbuf2, bufShapes2);
						lblCount2.setText(String.valueOf(cirbuf2.getOccupiedBufferCount()));
						double ratio = (double) cirbuf2.getOccupiedBufferCount() / cirbuf2.getBufSize();
						Utils.setBackground(lblCount2, Utils.getRatioColor(ratio));
						// System.out.println("lblCount Style = "+lblCount.getStyle());
						cirbuf3.write(sval);
					});
					// Put the thread into sleep
//					Thread.sleep((int)(Math.random() * 1000));
					Thread.sleep((int) (Math.random() * 500));

				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class Consumer3Task implements Runnable {
		public void run() {
			try {
				while (true) {
					String sval = cirbuf3.read();// 이 부근 에서 그냥 두개 서큐러 버퍼 중에서 1개 읽으면 될듯

					System.out.println("\t\t\tConsumer3 reads " + sval);
					Platform.runLater(() -> {
						lblNumber.setText("Consumer3");
						lblFiboC.setText(sval);
						setBufferShapeColor(cirbuf3, bufShapes3);
						lblCount3.setText(String.valueOf(cirbuf3.getOccupiedBufferCount()));
						double ratio = (double) cirbuf3.getOccupiedBufferCount() / cirbuf3.getBufSize();
						Utils.setBackground(lblCount3, Utils.getRatioColor(ratio));
						// System.out.println("lblCount Style = "+lblCount.getStyle());
					});
					// Put the thread into sleep
//					Thread.sleep((int)(Math.random() * 1000));
					Thread.sleep((int) (Math.random() * 250));

				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

}
