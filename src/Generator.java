import java.util.*;

public class Generator {

	private int width, height;
//	private Random generator = new Random(8);
	private int maxTime, maxProcessing;
	
	public Generator(int width, int height){
		this.setWidth(width);
		this.setHeight(height);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = maxTime;
	}

	public int getMaxProcessing() {
		return maxProcessing;
	}

	public void setMaxProcessing(int maxProcessing) {
		this.maxProcessing = maxProcessing;
	}

}
