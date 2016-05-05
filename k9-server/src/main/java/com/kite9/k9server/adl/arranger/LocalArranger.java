package com.kite9.k9server.adl.arranger;

import org.kite9.diagram.adl.Diagram;
import org.kite9.diagram.visualization.display.complete.ADLBasicCompleteDisplayer;
import org.kite9.diagram.visualization.display.complete.GriddedCompleteDisplayer;
import org.kite9.diagram.visualization.display.style.Stylesheet;
import org.kite9.diagram.visualization.display.style.sheets.BasicStylesheet;
import org.kite9.diagram.visualization.format.pos.PositionInfoRenderer;
import org.kite9.diagram.visualization.pipeline.full.ImageProcessingPipeline;
import org.kite9.diagram.visualization.pipeline.full.ProcessingPipeline;
import org.springframework.stereotype.Component;

import com.kite9.k9server.adl.StylesheetProvider;

/**
 * This contains the methods that turns an input XML diagram into a diagram with rendering information.
 * 
 * This is now the single point of access for creating arrangments of diagrams.
 * 
 * @author robmoffat
 *
 */
@Component
public class LocalArranger implements DiagramArranger  {
	
	
	public LocalArranger() {
		// check that we can load the arranger classes
		createPipeline(new BasicStylesheet());
	}
	
	/**
	 * Ensures a time limit on diagram generation of 20 seconds.
	 */
	protected Diagram timedDiagramGeneration(final Diagram d, final Stylesheet ss) throws Exception {
		final Thread me = Thread.currentThread();
		Thread timer = new Thread(new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(20000);
					me.interrupt();
				} catch (InterruptedException e) {
					// exit normally, the diagram is complete.
					System.out.println("Diagram completed ok");
				}
			}
		});
		
		try {
			timer.start();
			return doArrange(d, ss);
		} catch (Exception e) {
			// run out of time.
			System.out.println("Problem generating diagram (could be out of time)");
			throw e;
		} finally {
			timer.interrupt();
		}
	}

	private Diagram doArrange(Diagram d, Stylesheet ss) {
		return createPipeline(ss).process(d);

	}
	protected ProcessingPipeline<Diagram> createPipeline(Stylesheet ss) {
		return new ImageProcessingPipeline<Diagram>(new GriddedCompleteDisplayer(
				new ADLBasicCompleteDisplayer(ss, false, true), ss), new PositionInfoRenderer());
	}

	@Override
	public Diagram arrangeDiagram(Diagram input, String stylesheetName) throws Exception {
		Stylesheet ss = StylesheetProvider.getStylesheet(stylesheetName);
		return timedDiagramGeneration(input, ss);
	}
}
