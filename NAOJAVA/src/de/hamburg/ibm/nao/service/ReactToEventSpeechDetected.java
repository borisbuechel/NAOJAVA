package de.hamburg.ibm.nao.service;

import java.io.File;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALAudioRecorder;
import com.aldebaran.qi.helper.proxies.ALMemory;
import com.aldebaran.qi.helper.proxies.ALTextToSpeech;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

public class ReactToEventSpeechDetected {

	ALMemory memory;
	ALTextToSpeech tts;
	long frontTactilSubscriptionId;

	public void run(Session session) throws Exception {

		memory = new ALMemory(session);
		tts = new ALTextToSpeech(session);
		frontTactilSubscriptionId = 0;

		// Subscribe to FrontTactilTouched event,
		// create an EventCallback expecting a Float.
		frontTactilSubscriptionId = memory.subscribeToEvent("SpeechDetected", new EventCallback<Float>() {
			@Override
			public void onEvent(Float arg0) throws InterruptedException, CallError {
				try {
					// 1 means the sensor has been pressed
					if (arg0 > 0) {
						System.out.println("I understand you");
						tts.say("I understand you, now it is your turn");

						ALAudioRecorder ar;
						ar = new ALAudioRecorder(session);

						Boolean[] channels = { true, true, true, true };

						/// Starts the recording of NAO's front microphone at
						/// 16000Hz
						/// in the specified wav file
						ar.startMicrophonesRecording("C:/Users/XXX/Desktop/test.wav", "wav", 16000,
								channels);

						Thread.sleep(5000);

						/// Stops the recording and close the file after 10
						/// seconds.
						ar.stopMicrophonesRecording();

						SpeechToText service = new SpeechToText();
						service.setUsernameAndPassword("3cd51759-484d-4872-a715-c1070dd0XXXX", "IcIdnJhvXXXX");

						File audio = new File("C:/Users/XXX/Desktop/test.wav");

						SpeechResults transcript = service.recognize(audio).execute();

						System.out.println(transcript);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
