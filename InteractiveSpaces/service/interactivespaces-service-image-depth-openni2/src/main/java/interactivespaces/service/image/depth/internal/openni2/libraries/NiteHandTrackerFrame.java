package interactivespaces.service.image.depth.internal.openni2.libraries;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * <i>native declaration : NiteCTypes.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("NiTE2") 
public class NiteHandTrackerFrame extends StructObject {
	/** Number of hands */
	@Field(0) 
	public int handCount() {
		return this.io.getIntField(this, 0);
	}
	/** Number of hands */
	@Field(0) 
	public NiteHandTrackerFrame handCount(int handCount) {
		this.io.setIntField(this, 0, handCount);
		return this;
	}
	/**
	 * List of hands<br>
	 * C type : NiteHandData*
	 */
	@Field(1) 
	public Pointer<NiteHandData > pHands() {
		return this.io.getPointerField(this, 1);
	}
	/**
	 * List of hands<br>
	 * C type : NiteHandData*
	 */
	@Field(1) 
	public NiteHandTrackerFrame pHands(Pointer<NiteHandData > pHands) {
		this.io.setPointerField(this, 1, pHands);
		return this;
	}
	/** Number of gestures */
	@Field(2) 
	public int gestureCount() {
		return this.io.getIntField(this, 2);
	}
	/** Number of gestures */
	@Field(2) 
	public NiteHandTrackerFrame gestureCount(int gestureCount) {
		this.io.setIntField(this, 2, gestureCount);
		return this;
	}
	/**
	 * List of gestures<br>
	 * C type : NiteGestureData*
	 */
	@Field(3) 
	public Pointer<NiteGestureData > pGestures() {
		return this.io.getPointerField(this, 3);
	}
	/**
	 * List of gestures<br>
	 * C type : NiteGestureData*
	 */
	@Field(3) 
	public NiteHandTrackerFrame pGestures(Pointer<NiteGestureData > pGestures) {
		this.io.setPointerField(this, 3, pGestures);
		return this;
	}
	/**
	 * The depth frame from which this data was learned<br>
	 * C type : OniFrame*
	 */
	@Field(4) 
	public Pointer<OniFrame > pDepthFrame() {
		return this.io.getPointerField(this, 4);
	}
	/**
	 * The depth frame from which this data was learned<br>
	 * C type : OniFrame*
	 */
	@Field(4) 
	public NiteHandTrackerFrame pDepthFrame(Pointer<OniFrame > pDepthFrame) {
		this.io.setPointerField(this, 4, pDepthFrame);
		return this;
	}
	@Field(5) 
	public long timestamp() {
		return this.io.getLongField(this, 5);
	}
	@Field(5) 
	public NiteHandTrackerFrame timestamp(long timestamp) {
		this.io.setLongField(this, 5, timestamp);
		return this;
	}
	@Field(6) 
	public int frameIndex() {
		return this.io.getIntField(this, 6);
	}
	@Field(6) 
	public NiteHandTrackerFrame frameIndex(int frameIndex) {
		this.io.setIntField(this, 6, frameIndex);
		return this;
	}
	public NiteHandTrackerFrame() {
		super();
	}
	public NiteHandTrackerFrame(Pointer pointer) {
		super(pointer);
	}
}
