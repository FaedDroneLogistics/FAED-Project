package interactivespaces.service.image.depth.internal.openni2.libraries;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * <i>native declaration : OniCTypes.h</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("OpenNI2") 
public class OniVersion extends StructObject {
	/** Major version number, incremented for major API restructuring. */
	@Field(0) 
	public int major() {
		return this.io.getIntField(this, 0);
	}
	/** Major version number, incremented for major API restructuring. */
	@Field(0) 
	public OniVersion major(int major) {
		this.io.setIntField(this, 0, major);
		return this;
	}
	/** Minor version number, incremented when significant new features added. */
	@Field(1) 
	public int minor() {
		return this.io.getIntField(this, 1);
	}
	/** Minor version number, incremented when significant new features added. */
	@Field(1) 
	public OniVersion minor(int minor) {
		this.io.setIntField(this, 1, minor);
		return this;
	}
	/** Maintenance build number, incremented for new releases that primarily provide minor bug fixes. */
	@Field(2) 
	public int maintenance() {
		return this.io.getIntField(this, 2);
	}
	/** Maintenance build number, incremented for new releases that primarily provide minor bug fixes. */
	@Field(2) 
	public OniVersion maintenance(int maintenance) {
		this.io.setIntField(this, 2, maintenance);
		return this;
	}
	/** Build number. Incremented for each new API build. Generally not shown on the installer and download site. */
	@Field(3) 
	public int build() {
		return this.io.getIntField(this, 3);
	}
	/** Build number. Incremented for each new API build. Generally not shown on the installer and download site. */
	@Field(3) 
	public OniVersion build(int build) {
		this.io.setIntField(this, 3, build);
		return this;
	}
	public OniVersion() {
		super();
	}
	public OniVersion(Pointer pointer) {
		super(pointer);
	}
}
