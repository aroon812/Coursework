import java.awt.BasicStroke;
/**
 * Graphs the performance of various sorting algorithms
 * @author Aaron Thompson
 * @version 3/16/2017
 */
import java.awt.Color;
import java.awt.Graphics2D;
class GraphSorts 
{
	private static final int WINDOW_WIDTH = 1000;
	private static final int WINDOW_HEIGHT = 900;
	
	private static final double MAX_N = 50000;
	private static final int MARGIN_SIZE = 50;
	private static final int GRAPH_WIDTH = WINDOW_WIDTH - (2 * (MARGIN_SIZE));
	private static final int GRAPH_HEIGHT = WINDOW_HEIGHT - (2 * (MARGIN_SIZE));
	private static final double MAX_MS = 10000;
	int[] xArray = new int[10];
	int[] yArray = new int[10];
	
	/**
	 * Runs the sorting algorithms and converts the data into x and y coordinates
	 * @param sorter the object containing the sorting algorithm
	 */
	public void assignArrayValues(Sorter sorter)
	{
		int arrayIncrease = 5000;
		for(int i = arrayIncrease, j = 0; i <= MAX_N; i += arrayIncrease, j++)
		{
			xArray[j] = ConvertSizetoX(i);
			yArray[j] = ConvertMillisToY(sorter.timeSort(i));
		}
	}
	
	/**
	 * Draws the curves of x and y coordinates
	 * @param color the desired color of the curve
	 * @param pen the Graphics2D object
	 */
	public void drawCurve(Color color, Graphics2D pen)
	{
		pen.setColor(color);
		pen.drawLine(MARGIN_SIZE, MARGIN_SIZE+GRAPH_HEIGHT, xArray[0], yArray[0]);
		for(int i = 0; i < xArray.length-1; i++)
		{
			pen.drawLine(xArray[i], yArray[i], xArray[i+1], yArray[i+1]);
		}
	}
	
	//convert the data into x and y coordinates
	private int ConvertSizetoX(int size)
	{
		return (int)(size * GRAPH_WIDTH/MAX_N + 0.5) + MARGIN_SIZE;
	}
	
	private int ConvertMillisToY(int millis)
	{
		return GRAPH_HEIGHT - (int)(millis*GRAPH_HEIGHT/MAX_MS + 0.5) + MARGIN_SIZE;
	}
	
	public static void main(String[] args)
	{
		GraphSorts gSort = new GraphSorts();
		GraphicsWindow window = new GraphicsWindow("Sort Comparison", WINDOW_WIDTH, WINDOW_HEIGHT, 0, 0);
		Graphics2D pen = window.getPen();
		window.paintBackground(Color.WHITE);
		
		//draw the initial box
		pen.setColor(Color.BLACK);
		pen.drawLine(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, WINDOW_HEIGHT-MARGIN_SIZE);
		pen.drawLine(MARGIN_SIZE, WINDOW_HEIGHT-MARGIN_SIZE, WINDOW_WIDTH-MARGIN_SIZE, WINDOW_HEIGHT-MARGIN_SIZE);
		pen.drawLine(MARGIN_SIZE, MARGIN_SIZE, WINDOW_WIDTH-MARGIN_SIZE, MARGIN_SIZE);
		pen.drawLine(WINDOW_WIDTH-MARGIN_SIZE, MARGIN_SIZE, WINDOW_WIDTH-MARGIN_SIZE, WINDOW_HEIGHT-MARGIN_SIZE);
		
		pen.drawString("S", 0, WINDOW_HEIGHT/2);
		pen.drawString("n", WINDOW_WIDTH/2, WINDOW_HEIGHT);
		
		//add tick marks and labels
		int label = 10;
		int labelOffset = 10;
		for(int i = 0, tickSpace = MARGIN_SIZE; i <= 10; i++, tickSpace += GRAPH_HEIGHT/10)
		{
			pen.drawLine(MARGIN_SIZE, tickSpace, MARGIN_SIZE-labelOffset, tickSpace);
			pen.drawString(label + "", MARGIN_SIZE - (3*(labelOffset)), tickSpace+(labelOffset/2));
			label--;
		}
		
		label = 0;
		for(int i = 0, tickSpace = MARGIN_SIZE; i <= 10; i++, tickSpace += GRAPH_WIDTH/10)
		{
			pen.drawLine(tickSpace, WINDOW_HEIGHT-MARGIN_SIZE, tickSpace, WINDOW_HEIGHT-MARGIN_SIZE+labelOffset);
			pen.drawString(label + "", tickSpace, WINDOW_HEIGHT-MARGIN_SIZE+(labelOffset * 3));
			label += 5000;
		}
		
		//draw the key
		int keyOffset = 20;
		pen.setColor(Color.RED);
		pen.drawString("Bubble Sort", MARGIN_SIZE+labelOffset, MARGIN_SIZE+keyOffset);
		
		keyOffset += 20;
		pen.setColor(Color.GREEN);
		pen.drawString("Selection Sort", MARGIN_SIZE+labelOffset, MARGIN_SIZE+keyOffset);
		
		keyOffset += 20;
		pen.setColor(Color.BLUE);
		pen.drawString("Heap Sort", MARGIN_SIZE+labelOffset, MARGIN_SIZE+keyOffset);
		
		keyOffset += 20;
		pen.setColor(Color.ORANGE);
		pen.drawString("Merge Sort", MARGIN_SIZE+labelOffset, MARGIN_SIZE+keyOffset);
		
		pen.setStroke(new BasicStroke(3));
			
		//create all of the sorters, get the data, and draw it on the graph	
		MergeSorter mSorter = new MergeSorter();
		gSort.assignArrayValues(mSorter);
		gSort.drawCurve(Color.ORANGE, pen);
		
		HeapSorter hSorter = new HeapSorter();
		gSort.assignArrayValues(hSorter);
		gSort.drawCurve(Color.BLUE, pen);
		
		SelectionSorter sSorter = new SelectionSorter();
		gSort.assignArrayValues(sSorter);
		gSort.drawCurve(Color.GREEN, pen);
		
		BubbleSorter bSorter = new BubbleSorter();
		gSort.assignArrayValues(bSorter);
		gSort.drawCurve(Color.RED, pen);
		
		window.finalize();
		
	}
	
}
