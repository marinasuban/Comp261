package code;

// DO NOT DISTRIBUTE THIS FILE TO STUDENTS
import ecs100.UI;
import ecs100.UIFileChooser;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/*
  getAudioInputStream
  -> getframelength,
  -> read into byteArray of 2x that many bytes
  -> convert to array of doubles in reversed pairs of bytes (signed)
  -> scale #FFFF to +/- 300

  array of doubles
   -> unscale  +/- 300  to #FFFF (
   -> convert to array of bytes (pairs little endian, signed)
   -> convert to inputStream
   -> convert to AudioInputStream
   -> write to file.
 */

public class SoundWaveform {

	public static final double MAX_VALUE = 300;
	public static final int SAMPLE_RATE = 44100;
	public static final int MAX_SAMPLES = SAMPLE_RATE / 100; // samples in 1/100 sec

	public static final int GRAPH_LEFT = 10;
	public static final int ZERO_LINE = 310;
	public static final int X_STEP = 2; // pixels between samples
	public static final int GRAPH_WIDTH = MAX_SAMPLES * X_STEP;

	private ArrayList<Double>  waveform = new ArrayList<Double>(); // the displayed waveform
	private ArrayList<ComplexNumber> spectrum = new ArrayList<ComplexNumber>(); // the spectrum: length/mod of each X(k)

	/**
	 * Displays the waveform.
	 */
	public void displayWaveform() {
		if (this.waveform == null) { // there is no data to display
			UI.println("No waveform to display");
			return;
		}
		UI.clearText();
		UI.println("Printing, please wait...");

		UI.clearGraphics();

		// draw x axis (showing where the value 0 will be)
		UI.setColor(Color.black);
		UI.drawLine(GRAPH_LEFT, ZERO_LINE, GRAPH_LEFT + GRAPH_WIDTH, ZERO_LINE);

		// plot points: blue line between each pair of values
		UI.setColor(Color.blue);

		double x = GRAPH_LEFT;
		for (int i = 1; i < this.waveform.size(); i++) {
			double y1 = ZERO_LINE - this.waveform.get(i - 1);
			double y2 = ZERO_LINE - this.waveform.get(i);
			if (i > MAX_SAMPLES) {
				UI.setColor(Color.red);
			}
			UI.drawLine(x, y1, x + X_STEP, y2);
			x = x + X_STEP;
		}

		UI.println("Printing completed!");
	}

	/**
	 * Displays the spectrum. Scale to the range of +/- 300.
	 */
	public void displaySpectrum() {
		if (this.spectrum == null) { // there is no data to display
			UI.println("No spectrum to display");
			return;
		}
		UI.clearText();
		UI.println("Printing, please wait...");

		UI.clearGraphics();

		// calculate the mode of each element
		ArrayList<Double> spectrumMod = new ArrayList<Double>();
		double max = 0;
		for (int i = 0; i < spectrum.size(); i++) {
			if (i == MAX_SAMPLES)
				break;

			double value = spectrum.get(i).mod();
			max = Math.max(max, value);
			spectrumMod.add(spectrum.get(i).mod());
		}

		double scaling = 300 / max;
		for (int i = 0; i < spectrumMod.size(); i++) {
			spectrumMod.set(i, spectrumMod.get(i) * scaling);
		}

		// draw x axis (showing where the value 0 will be)
		UI.setColor(Color.black);
		UI.drawLine(GRAPH_LEFT, ZERO_LINE, GRAPH_LEFT + GRAPH_WIDTH, ZERO_LINE);

		// plot points: blue line between each pair of values
		UI.setColor(Color.blue);

		double x = GRAPH_LEFT;
		for (int i = 1; i < spectrumMod.size(); i++) {
			double y1 = ZERO_LINE;
			double y2 = ZERO_LINE - spectrumMod.get(i);
			if (i > MAX_SAMPLES) {
				UI.setColor(Color.red);
			}
			UI.drawLine(x, y1, x + X_STEP, y2);
			x = x + X_STEP;
		}

		UI.println("Printing completed!");
	}

	/**
	 * https://www.nayuki.io/page/how-to-implement-the-discrete-fourier-transform
	 * Given a list of doubles we return a list of complex create empty CN list,
	 * loop over i waveform to create CN to store result loop over k waveform, for
	 * each index we get value of e ^ (-i*n*k*2*pi/N) and multiply it. the result is
	 * then stored in index index is added finalL which is then added to spectrum
	 */
	public void dft() {
		UI.clearText();
		Instant start = Instant.now();
		UI.println("DFT in process, please wait...");

		//TEST
//      ArrayList<Double> case1 = new ArrayList(Arrays.asList(1.0 ,2.0 ,3.0 ,4.0 ,5.0, 6.0, 7.0, 8.0));
//      ArrayList<Double> case2 = new ArrayList(Arrays.asList(1.0 ,2.0 ,1.0 ,2.0 ,1.0 ,2.0 ,1.0 ,2.0));
//      ArrayList<Double> case3 = new ArrayList(Arrays.asList(1.0 ,2.0 ,3.0 ,4.0 , 4.0 ,3.0, 2.0, 1.0));
//      waveform = case1;


		ArrayList<ComplexNumber> finalL = new ArrayList<>();
		for (int i = 0; i < waveform.size(); i++) {
			ComplexNumber index = new ComplexNumber();
			for (int k = 0; k < waveform.size(); k++) {
				ComplexNumber power = new ComplexNumber(0, -k * i * 2 * Math.PI / waveform.size());
				ComplexNumber result = power.powerOf(Math.E);
				result = result.multiply(new ComplexNumber(waveform.get(k), 0));
				index.add(result);
			}
			finalL.add(index);
		}
		spectrum = finalL;
		UI.println("Spectrum: " + spectrum);
		UI.println("DFT completed!");
		Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        double seconds = (double)timeElapsed.toMillis()/1000;
        UI.println("Time taken: "+ seconds +" seconds");
		waveform.clear();
	}

	/**
	 * https://www.nayuki.io/page/how-to-implement-the-discrete-fourier-transform
	 * Given a list of complex we return a list of Double create empty D list, loop
	 * over i spectrum to create CN to store result loop over k spectrum, for each
	 * index we get value of e ^ (i*n*k*2*pi/N) and multiply it. the result is then
	 * stored in index index real / spectrum size is added finalL which is then
	 * added to waveform
	 */
	public void idft() {
		UI.clearText();
		Instant start = Instant.now();
		UI.println("IDFT in process, please wait...");

		ArrayList<Double> finalL = new ArrayList<>();
		for (int i = 0; i < spectrum.size(); i++) {
			ComplexNumber index = new ComplexNumber();
			for (int k = 0; k < spectrum.size(); k++) {
				ComplexNumber power = new ComplexNumber(0, i * k * 2 * Math.PI / spectrum.size());
				ComplexNumber result = power.powerOf(Math.E);
				result = result.multiply(spectrum.get(k));
				index.add(result);
			}
			finalL.add(index.getRe() / spectrum.size());
		}
		waveform = finalL;
		UI.println("Waveform: " + waveform);
		UI.println("IDFT completed!");
		Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        double seconds = (double)timeElapsed.toMillis()/1000;
        UI.println("Time taken: "+ seconds +" seconds");
		spectrum.clear();
	}

	public void fft() {
		UI.clearText();
		Instant start = Instant.now();
		if (waveform.size() == 0)
			return;
		UI.clearText();
		UI.println("FFT in process, please wait...");

		int power = 0;
		while (Math.pow(2, power + 1) <= waveform.size()) {
			power++;
		}
		List<Double> trimmed = waveform.subList(0, (int) Math.pow(2, power));
		List<ComplexNumber> waveCN = new ArrayList<>();
		for (Double d : trimmed) {
			waveCN.add(new ComplexNumber(d, 0));
		}
		spectrum = (ArrayList<ComplexNumber>) fft(waveCN, false);
		UI.println("FFT completed!");
		displaySpectrum();
		Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        double seconds = (double)timeElapsed.toMillis()/1000;
        UI.println("Time taken: "+ seconds +" seconds");
	}

	private List<ComplexNumber> fft(List<ComplexNumber> wave, boolean isInverse) {
		List<ComplexNumber> waveResult = new ArrayList<>();
		int waveSize = wave.size();

		// if only one value in the list return result
		if (waveSize == 1) {
			waveResult.add(wave.get(0));
			return waveResult;
		}

		List<ComplexNumber> even = new ArrayList<>();
		List<ComplexNumber> odd = new ArrayList<>();

		// split into even and odd list using remainder of i
		for (int i = 0; i < wave.size(); i++) {
			if (i % 2 == 0) {
				even.add(wave.get(i));
			} else {
				odd.add(wave.get(i));
			}
		}
		List<ComplexNumber> xEven = fft(even, isInverse);
		List<ComplexNumber> xOdd = fft(odd, isInverse);
		List<ComplexNumber> waveResult1 = new ArrayList<>();

		for (int k = 0; k < wave.size() / 2; k++) {
			ComplexNumber kcomplex = new ComplexNumber(0, -k * 2 * Math.PI / waveSize);
			ComplexNumber halfKcomplex = new ComplexNumber(0, -(k + waveSize / 2) * 2 * Math.PI / waveSize);

			if (isInverse) {
				kcomplex = kcomplex.conjugate();
				halfKcomplex = halfKcomplex.conjugate();
			}

			ComplexNumber powerK = kcomplex.powerOf(Math.E);
			ComplexNumber powerhalfK = halfKcomplex.powerOf(Math.E);

			ComplexNumber finalK = xOdd.get(k).multiply(powerK);
			finalK.add(xEven.get(k));
			waveResult.add(finalK);

			ComplexNumber finalhalfK = xOdd.get(k).multiply(powerhalfK);
			finalhalfK.add(xEven.get(k));
			waveResult1.add(finalhalfK);
		}
		waveResult.addAll(waveResult1);
		return waveResult;
	}

	public void ifft() {
		UI.clearText();
		Instant start = Instant.now();
		UI.println("IFFT in process, please wait...");
		ArrayList<Double> newWaveForm = new ArrayList<>();
		int power = 0;
		while (Math.pow(2, power + 1) <= spectrum.size()) {
			power++;
		}
		List<ComplexNumber> trimmed = spectrum.subList(0, (int) Math.pow(2, power));
		List<ComplexNumber> wave = fft(trimmed, true);
		for (ComplexNumber C : wave) {
			newWaveForm.add(C.getRe() / wave.size());
		}
		waveform = newWaveForm;
		UI.println("IFFT completed!");
		displayWaveform();
		Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        double seconds = (double)timeElapsed.toMillis()/1000;
        UI.println("Time taken: "+ seconds +" seconds");
	}

	/**
	 * Save the wave form to a WAV file
	 */
	public void doSave() {
		WaveformLoader.doSave(waveform, WaveformLoader.scalingForSavingFile);
	}

	/**
	 * Load the WAV file.
	 */
	public void doLoad() {
		UI.clearText();
		UI.println("Loading...");

		waveform = WaveformLoader.doLoad();

		this.displayWaveform();

		UI.println("Loading completed!");
	}

	public static void main(String[] args) {
		SoundWaveform wfm = new SoundWaveform();
		// core
		UI.addButton("Display Waveform", wfm::displayWaveform);
		UI.addButton("Display Spectrum", wfm::displaySpectrum);
		UI.addButton("DFT", wfm::dft);
		UI.addButton("IDFT", wfm::idft);
		UI.addButton("FFT", wfm::fft);
		UI.addButton("IFFT", wfm::ifft);
		UI.addButton("Save", wfm::doSave);
		UI.addButton("Load", wfm::doLoad);
		UI.addButton("Quit", UI::quit);
		// UI.setMouseMotionListener(wfm::doMouse);
		UI.setWindowSize(950, 630);
	}
}
