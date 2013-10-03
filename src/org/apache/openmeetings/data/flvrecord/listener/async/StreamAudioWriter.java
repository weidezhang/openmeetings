/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.data.flvrecord.listener.async;

import static org.apache.openmeetings.OpenmeetingsVariables.webAppRootKey;

import java.util.Date;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.openmeetings.data.flvrecord.FlvRecordingMetaDataDao;
import org.apache.openmeetings.data.flvrecord.FlvRecordingMetaDeltaDao;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaDelta;
import org.red5.io.ITag;
import org.red5.io.IoConstants;
import org.red5.io.flv.impl.Tag;
import org.red5.logging.Red5LoggerFactory;
import org.red5.server.api.scope.IScope;
import org.red5.server.net.rtmp.event.VideoData.FrameType;
import org.slf4j.Logger;

public class StreamAudioWriter extends BaseStreamWriter {
	private static final Logger log = Red5LoggerFactory.getLogger(StreamAudioWriter.class, webAppRootKey);

	private int duration = 0;

	private int startTimeStamp = -1;

	private long initialDelta = 0;

	private Integer lastTimeStamp = -1;
	private Date lastcurrentTime = null;

	private int lastStreamPacketTimeStamp = -1;

	private long byteCount = 0;

	// Autowire is not possible
	protected final FlvRecordingMetaDeltaDao metaDeltaDao;
	protected final FlvRecordingMetaDataDao metaDataDao;

	private boolean isInterview = false;

	public StreamAudioWriter(String streamName, IScope scope, Long flvRecordingMetaDataId, boolean isScreenData,
			boolean isInterview, FlvRecordingMetaDataDao metaDataDao, FlvRecordingMetaDeltaDao metaDeltaDao) {
		super(streamName, scope, flvRecordingMetaDataId, isScreenData);

		this.metaDeltaDao = metaDeltaDao;
		this.metaDataDao = metaDataDao;
		this.isInterview = isInterview;

		FlvRecordingMetaData flvRecordingMetaData = metaDataDao
				.getFlvRecordingMetaDataById(flvRecordingMetaDataId);
		flvRecordingMetaData.setStreamReaderThreadComplete(false);
		metaDataDao.updateFlvRecordingMetaData(flvRecordingMetaData);
	}

	@Override
	public void packetReceived(CachedEvent streampacket) {
		try {
			// We only care about audio at this moment
			if (isInterview || streampacket.getDataType() == IoConstants.TYPE_AUDIO) {
				if (streampacket.getTimestamp() <= 0) {
					log.warn("Negative TimeStamp");
					return;
				}
				if (isInterview && startTimeStamp == -1 && FrameType.KEYFRAME != streampacket.getFrameType()) {
					//skip until keyframe
					return;
				}
				IoBuffer data = streampacket.getData().asReadOnlyBuffer();
				if (data.limit() == 0) {
					return;
				}

				byteCount += data.limit();

				lastcurrentTime = streampacket.getCurrentTime();
				int timeStamp = streampacket.getTimestamp();
				Date virtualTime = streampacket.getCurrentTime();

				if (startTimeStamp == -1) {

					// Calculate the delta between the initial start and the
					// first audio-packet data

					initialDelta = virtualTime.getTime() - startedSessionTimeDate.getTime();

					FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();

					flvRecordingMetaDelta.setDeltaTime(initialDelta);
					flvRecordingMetaDelta.setFlvRecordingMetaDataId(flvRecordingMetaDataId);
					flvRecordingMetaDelta.setTimeStamp(0);
					flvRecordingMetaDelta.setDebugStatus("INIT AUDIO");
					flvRecordingMetaDelta.setIsStartPadding(true);
					flvRecordingMetaDelta.setIsEndPadding(false);
					flvRecordingMetaDelta.setDataLengthPacket(data.limit());
					flvRecordingMetaDelta.setReceivedAudioDataLength(byteCount);
					flvRecordingMetaDelta.setStartTime(startedSessionTimeDate);
					flvRecordingMetaDelta.setPacketTimeStamp(streampacket.getTimestamp());

					Long deltaTimeStamp = virtualTime.getTime() - startedSessionTimeDate.getTime();

					// duration = Math.max(duration, 0 +
					// writer.getOffset());
					flvRecordingMetaDelta.setDuration(0);

					Long missingTime = deltaTimeStamp - 0;

					flvRecordingMetaDelta.setMissingTime(missingTime);

					flvRecordingMetaDelta.setCurrentTime(virtualTime);
					flvRecordingMetaDelta.setDeltaTimeStamp(deltaTimeStamp);
					flvRecordingMetaDelta.setStartTimeStamp(startTimeStamp);

					metaDeltaDao.addFlvRecordingMetaDelta(flvRecordingMetaDelta);

					// That will be not bigger then long value
					startTimeStamp = (streampacket.getTimestamp());

					// We have to set that to bypass the initial delta
					// lastTimeStamp = startTimeStamp;
				}

				lastStreamPacketTimeStamp = streampacket.getTimestamp();

				timeStamp -= startTimeStamp;

				long deltaTime = 0;
				if (lastTimeStamp == -1) {
					deltaTime = 0; // Offset at the beginning is calculated
									// above
				} else {
					deltaTime = timeStamp - lastTimeStamp;
				}

				Long preLastTimeStamp = Long.parseLong(lastTimeStamp.toString());

				lastTimeStamp = timeStamp;

				if (deltaTime > 75) {

					FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();

					flvRecordingMetaDelta.setDeltaTime(deltaTime);
					flvRecordingMetaDelta.setFlvRecordingMetaDataId(flvRecordingMetaDataId);
					flvRecordingMetaDelta.setTimeStamp(timeStamp);
					flvRecordingMetaDelta.setDebugStatus("RUN AUDIO");
					flvRecordingMetaDelta.setIsStartPadding(false);
					flvRecordingMetaDelta.setLastTimeStamp(preLastTimeStamp);
					flvRecordingMetaDelta.setIsEndPadding(false);
					flvRecordingMetaDelta.setDataLengthPacket(data.limit());
					flvRecordingMetaDelta.setReceivedAudioDataLength(byteCount);
					flvRecordingMetaDelta.setStartTime(startedSessionTimeDate);
					flvRecordingMetaDelta.setPacketTimeStamp(streampacket.getTimestamp());

					Date current_date = new Date();
					Long deltaTimeStamp = current_date.getTime() - startedSessionTimeDate.getTime();

					duration = Math.max(duration, timeStamp + writer.getOffset());
					flvRecordingMetaDelta.setDuration(duration);

					Long missingTime = deltaTimeStamp - timeStamp;

					flvRecordingMetaDelta.setMissingTime(missingTime);

					flvRecordingMetaDelta.setCurrentTime(current_date);
					flvRecordingMetaDelta.setDeltaTimeStamp(deltaTimeStamp);
					flvRecordingMetaDelta.setStartTimeStamp(startTimeStamp);

					metaDeltaDao.addFlvRecordingMetaDelta(flvRecordingMetaDelta);

				}

				ITag tag = new Tag();
				tag.setDataType(streampacket.getDataType());

				// log.debug("data.limit() :: "+data.limit());
				tag.setBodySize(data.limit());
				tag.setTimestamp(timeStamp);
				tag.setBody(data);

				writer.writeTag(tag);

			}
		} catch (Exception e) {
			log.error("[packetReceived]", e);
		}
	}

	@Override
	public void closeStream() {
		try {
			writer.close();
		} catch (Exception err) {
			log.error("[closeStream, close writer]", err);
		}

		try {
			// We do not add any End Padding or count the gaps for the
			// Screen Data, cause there is no!

			Date virtualTime = lastcurrentTime;
			log.debug("virtualTime: " + virtualTime);
			log.debug("startedSessionTimeDate: " + startedSessionTimeDate);

			long deltaRecordingTime = virtualTime == null ? 0 : virtualTime.getTime()
					- startedSessionTimeDate.getTime();

			log.debug("lastTimeStamp :closeStream: " + lastTimeStamp);
			log.debug("lastStreamPacketTimeStamp :closeStream: " + lastStreamPacketTimeStamp);
			log.debug("deltaRecordingTime :closeStream: " + deltaRecordingTime);

			long deltaTimePaddingEnd = deltaRecordingTime - lastTimeStamp - initialDelta;

			log.debug("deltaTimePaddingEnd :: " + deltaTimePaddingEnd);

			FlvRecordingMetaDelta flvRecordingMetaDelta = new FlvRecordingMetaDelta();

			flvRecordingMetaDelta.setDeltaTime(deltaTimePaddingEnd);
			flvRecordingMetaDelta.setFlvRecordingMetaDataId(flvRecordingMetaDataId);
			flvRecordingMetaDelta.setTimeStamp(lastTimeStamp);
			flvRecordingMetaDelta.setDebugStatus("END AUDIO");
			flvRecordingMetaDelta.setIsStartPadding(false);
			flvRecordingMetaDelta.setIsEndPadding(true);
			flvRecordingMetaDelta.setDataLengthPacket(null);
			flvRecordingMetaDelta.setReceivedAudioDataLength(byteCount);
			flvRecordingMetaDelta.setStartTime(startedSessionTimeDate);
			flvRecordingMetaDelta.setCurrentTime(new Date());

			metaDeltaDao.addFlvRecordingMetaDelta(flvRecordingMetaDelta);

			// Write the complete Bit to the meta data, the converter task will wait for this bit!
			FlvRecordingMetaData flvRecordingMetaData = metaDataDao
					.getFlvRecordingMetaDataById(flvRecordingMetaDataId);
			flvRecordingMetaData.setStreamReaderThreadComplete(true);
			metaDataDao.updateFlvRecordingMetaData(flvRecordingMetaData);

		} catch (Exception err) {
			log.error("[closeStream]", err);
		}
	}
}
