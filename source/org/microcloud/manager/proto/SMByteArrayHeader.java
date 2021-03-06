// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: SMByteArrayHeader.proto

public final class SMByteArrayHeader {
  private SMByteArrayHeader() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface ByteArrayHeaderOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // required string datasource = 1;
    /**
     * <code>required string datasource = 1;</code>
     */
    boolean hasDatasource();
    /**
     * <code>required string datasource = 1;</code>
     */
    java.lang.String getDatasource();
    /**
     * <code>required string datasource = 1;</code>
     */
    com.google.protobuf.ByteString
        getDatasourceBytes();

    // required string filename = 2;
    /**
     * <code>required string filename = 2;</code>
     */
    boolean hasFilename();
    /**
     * <code>required string filename = 2;</code>
     */
    java.lang.String getFilename();
    /**
     * <code>required string filename = 2;</code>
     */
    com.google.protobuf.ByteString
        getFilenameBytes();

    // required int64 offset = 3;
    /**
     * <code>required int64 offset = 3;</code>
     */
    boolean hasOffset();
    /**
     * <code>required int64 offset = 3;</code>
     */
    long getOffset();

    // required int64 size = 4;
    /**
     * <code>required int64 size = 4;</code>
     */
    boolean hasSize();
    /**
     * <code>required int64 size = 4;</code>
     */
    long getSize();
  }
  /**
   * Protobuf type {@code ByteArrayHeader}
   */
  public static final class ByteArrayHeader extends
      com.google.protobuf.GeneratedMessage
      implements ByteArrayHeaderOrBuilder {
    // Use ByteArrayHeader.newBuilder() to construct.
    private ByteArrayHeader(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private ByteArrayHeader(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final ByteArrayHeader defaultInstance;
    public static ByteArrayHeader getDefaultInstance() {
      return defaultInstance;
    }

    public ByteArrayHeader getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private ByteArrayHeader(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              bitField0_ |= 0x00000001;
              datasource_ = input.readBytes();
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              filename_ = input.readBytes();
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              offset_ = input.readInt64();
              break;
            }
            case 32: {
              bitField0_ |= 0x00000008;
              size_ = input.readInt64();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return SMByteArrayHeader.internal_static_ByteArrayHeader_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return SMByteArrayHeader.internal_static_ByteArrayHeader_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              SMByteArrayHeader.ByteArrayHeader.class, SMByteArrayHeader.ByteArrayHeader.Builder.class);
    }

    public static com.google.protobuf.Parser<ByteArrayHeader> PARSER =
        new com.google.protobuf.AbstractParser<ByteArrayHeader>() {
      public ByteArrayHeader parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ByteArrayHeader(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<ByteArrayHeader> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // required string datasource = 1;
    public static final int DATASOURCE_FIELD_NUMBER = 1;
    private java.lang.Object datasource_;
    /**
     * <code>required string datasource = 1;</code>
     */
    public boolean hasDatasource() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string datasource = 1;</code>
     */
    public java.lang.String getDatasource() {
      java.lang.Object ref = datasource_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          datasource_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string datasource = 1;</code>
     */
    public com.google.protobuf.ByteString
        getDatasourceBytes() {
      java.lang.Object ref = datasource_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        datasource_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    // required string filename = 2;
    public static final int FILENAME_FIELD_NUMBER = 2;
    private java.lang.Object filename_;
    /**
     * <code>required string filename = 2;</code>
     */
    public boolean hasFilename() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required string filename = 2;</code>
     */
    public java.lang.String getFilename() {
      java.lang.Object ref = filename_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          filename_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string filename = 2;</code>
     */
    public com.google.protobuf.ByteString
        getFilenameBytes() {
      java.lang.Object ref = filename_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        filename_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    // required int64 offset = 3;
    public static final int OFFSET_FIELD_NUMBER = 3;
    private long offset_;
    /**
     * <code>required int64 offset = 3;</code>
     */
    public boolean hasOffset() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required int64 offset = 3;</code>
     */
    public long getOffset() {
      return offset_;
    }

    // required int64 size = 4;
    public static final int SIZE_FIELD_NUMBER = 4;
    private long size_;
    /**
     * <code>required int64 size = 4;</code>
     */
    public boolean hasSize() {
      return ((bitField0_ & 0x00000008) == 0x00000008);
    }
    /**
     * <code>required int64 size = 4;</code>
     */
    public long getSize() {
      return size_;
    }

    private void initFields() {
      datasource_ = "";
      filename_ = "";
      offset_ = 0L;
      size_ = 0L;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      if (!hasDatasource()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasFilename()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasOffset()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasSize()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(1, getDatasourceBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, getFilenameBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeInt64(3, offset_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        output.writeInt64(4, size_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, getDatasourceBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, getFilenameBytes());
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(3, offset_);
      }
      if (((bitField0_ & 0x00000008) == 0x00000008)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(4, size_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static SMByteArrayHeader.ByteArrayHeader parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static SMByteArrayHeader.ByteArrayHeader parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static SMByteArrayHeader.ByteArrayHeader parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static SMByteArrayHeader.ByteArrayHeader parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static SMByteArrayHeader.ByteArrayHeader parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static SMByteArrayHeader.ByteArrayHeader parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static SMByteArrayHeader.ByteArrayHeader parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static SMByteArrayHeader.ByteArrayHeader parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static SMByteArrayHeader.ByteArrayHeader parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static SMByteArrayHeader.ByteArrayHeader parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(SMByteArrayHeader.ByteArrayHeader prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code ByteArrayHeader}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements SMByteArrayHeader.ByteArrayHeaderOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return SMByteArrayHeader.internal_static_ByteArrayHeader_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return SMByteArrayHeader.internal_static_ByteArrayHeader_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                SMByteArrayHeader.ByteArrayHeader.class, SMByteArrayHeader.ByteArrayHeader.Builder.class);
      }

      // Construct using SMByteArrayHeader.ByteArrayHeader.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        datasource_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        filename_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        offset_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000004);
        size_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return SMByteArrayHeader.internal_static_ByteArrayHeader_descriptor;
      }

      public SMByteArrayHeader.ByteArrayHeader getDefaultInstanceForType() {
        return SMByteArrayHeader.ByteArrayHeader.getDefaultInstance();
      }

      public SMByteArrayHeader.ByteArrayHeader build() {
        SMByteArrayHeader.ByteArrayHeader result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public SMByteArrayHeader.ByteArrayHeader buildPartial() {
        SMByteArrayHeader.ByteArrayHeader result = new SMByteArrayHeader.ByteArrayHeader(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.datasource_ = datasource_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.filename_ = filename_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.offset_ = offset_;
        if (((from_bitField0_ & 0x00000008) == 0x00000008)) {
          to_bitField0_ |= 0x00000008;
        }
        result.size_ = size_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof SMByteArrayHeader.ByteArrayHeader) {
          return mergeFrom((SMByteArrayHeader.ByteArrayHeader)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(SMByteArrayHeader.ByteArrayHeader other) {
        if (other == SMByteArrayHeader.ByteArrayHeader.getDefaultInstance()) return this;
        if (other.hasDatasource()) {
          bitField0_ |= 0x00000001;
          datasource_ = other.datasource_;
          onChanged();
        }
        if (other.hasFilename()) {
          bitField0_ |= 0x00000002;
          filename_ = other.filename_;
          onChanged();
        }
        if (other.hasOffset()) {
          setOffset(other.getOffset());
        }
        if (other.hasSize()) {
          setSize(other.getSize());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasDatasource()) {
          
          return false;
        }
        if (!hasFilename()) {
          
          return false;
        }
        if (!hasOffset()) {
          
          return false;
        }
        if (!hasSize()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        SMByteArrayHeader.ByteArrayHeader parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (SMByteArrayHeader.ByteArrayHeader) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // required string datasource = 1;
      private java.lang.Object datasource_ = "";
      /**
       * <code>required string datasource = 1;</code>
       */
      public boolean hasDatasource() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string datasource = 1;</code>
       */
      public java.lang.String getDatasource() {
        java.lang.Object ref = datasource_;
        if (!(ref instanceof java.lang.String)) {
          java.lang.String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          datasource_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string datasource = 1;</code>
       */
      public com.google.protobuf.ByteString
          getDatasourceBytes() {
        java.lang.Object ref = datasource_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          datasource_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string datasource = 1;</code>
       */
      public Builder setDatasource(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        datasource_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string datasource = 1;</code>
       */
      public Builder clearDatasource() {
        bitField0_ = (bitField0_ & ~0x00000001);
        datasource_ = getDefaultInstance().getDatasource();
        onChanged();
        return this;
      }
      /**
       * <code>required string datasource = 1;</code>
       */
      public Builder setDatasourceBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        datasource_ = value;
        onChanged();
        return this;
      }

      // required string filename = 2;
      private java.lang.Object filename_ = "";
      /**
       * <code>required string filename = 2;</code>
       */
      public boolean hasFilename() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required string filename = 2;</code>
       */
      public java.lang.String getFilename() {
        java.lang.Object ref = filename_;
        if (!(ref instanceof java.lang.String)) {
          java.lang.String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          filename_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string filename = 2;</code>
       */
      public com.google.protobuf.ByteString
          getFilenameBytes() {
        java.lang.Object ref = filename_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          filename_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string filename = 2;</code>
       */
      public Builder setFilename(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        filename_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string filename = 2;</code>
       */
      public Builder clearFilename() {
        bitField0_ = (bitField0_ & ~0x00000002);
        filename_ = getDefaultInstance().getFilename();
        onChanged();
        return this;
      }
      /**
       * <code>required string filename = 2;</code>
       */
      public Builder setFilenameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        filename_ = value;
        onChanged();
        return this;
      }

      // required int64 offset = 3;
      private long offset_ ;
      /**
       * <code>required int64 offset = 3;</code>
       */
      public boolean hasOffset() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required int64 offset = 3;</code>
       */
      public long getOffset() {
        return offset_;
      }
      /**
       * <code>required int64 offset = 3;</code>
       */
      public Builder setOffset(long value) {
        bitField0_ |= 0x00000004;
        offset_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 offset = 3;</code>
       */
      public Builder clearOffset() {
        bitField0_ = (bitField0_ & ~0x00000004);
        offset_ = 0L;
        onChanged();
        return this;
      }

      // required int64 size = 4;
      private long size_ ;
      /**
       * <code>required int64 size = 4;</code>
       */
      public boolean hasSize() {
        return ((bitField0_ & 0x00000008) == 0x00000008);
      }
      /**
       * <code>required int64 size = 4;</code>
       */
      public long getSize() {
        return size_;
      }
      /**
       * <code>required int64 size = 4;</code>
       */
      public Builder setSize(long value) {
        bitField0_ |= 0x00000008;
        size_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 size = 4;</code>
       */
      public Builder clearSize() {
        bitField0_ = (bitField0_ & ~0x00000008);
        size_ = 0L;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:ByteArrayHeader)
    }

    static {
      defaultInstance = new ByteArrayHeader(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:ByteArrayHeader)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_ByteArrayHeader_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_ByteArrayHeader_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\027SMByteArrayHeader.proto\"U\n\017ByteArrayHe" +
      "ader\022\022\n\ndatasource\030\001 \002(\t\022\020\n\010filename\030\002 \002" +
      "(\t\022\016\n\006offset\030\003 \002(\003\022\014\n\004size\030\004 \002(\003"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_ByteArrayHeader_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_ByteArrayHeader_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_ByteArrayHeader_descriptor,
              new java.lang.String[] { "Datasource", "Filename", "Offset", "Size", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
