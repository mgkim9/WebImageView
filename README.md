# WebImageView(1.0.2)
* * *
[ ![Download](https://api.bintray.com/packages/mgkim/maven/webimageview/images/download.svg?version=1.0.0) ](https://bintray.com/mgkim/maven/webimageview/1.0.0/link)
- 2019.11.25
- 최초 업로드

[ ![Download](https://api.bintray.com/packages/mgkim/maven/webimageview/images/download.svg?version=1.0.1) ](https://bintray.com/mgkim/maven/webimageview/1.0.1/link)
- 2019.11.27
- Global Config 추가
- CacheWebImageView 삭제 (Config 속성으로 대채)
- DiskCache 관련 버그 수정

[ ![Download](https://api.bintray.com/packages/mgkim/maven/webimageview/images/download.svg) ](https://bintray.com/mgkim/maven/webimageview/_latestVersion)
- 2019.11.28
- lambda식 추가

* * *
## 설명
#### WebImageView
- 간편하게 Image를 다운로드하고 ImageView에 표시
- Disk / Memory Cache 적용
- Default ImageHolder, FileImage, Loding Progress 
- 여러차례 Request수행시 이전 Request Cancel 처리
- FIFO 지원
- Glide 보다 빠른 ImageLoad

#### RequestAPI
 - 쉬운 Json api 요청

#### RequestLocal
 - AsyncTask 대신 간편하게 Background 작업 수행

* * *
- ## API 문서 : [document](http://htmlpreview.github.com/?https://github.com/mgkim9/WebImageView/blob/1.0.2/javadocs/webimageview/index.html)
* * *

### dependencies:
    repositories {
        maven { url 'http://dl.bintray.com/mgkim/maven/'}
    }
    dependencies {
        // webimageview
        implementation 'com.mgkim.webimageview:webimageview:1.0.1@aar'
    }

### Sample:
[WebImageView-Sample](https://github.com/mgkim9/WebImageView-Sample)


1.기능
=============

1.이미지 다운로더
-------------
[WebImageView](https://htmlpreview.github.io/?https://raw.githubusercontent.com/mgkim9/WebImageView/1.0.2/javadocs/webimageview/com.mgkim.libs.webimageview.widget/-web-image-view/index.html) (Memory, File cache 사용)

### exemple 1) 기본 이미지 다운로드
### xml:
    <com.mgkim.mgkimlib.net.widget.WebImageView
      android:id="@+id/iv_image"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content" />
### src:
    val = url = "https://via.placeholder.com/600/6dd9cb"
    val ivImage: WebImageView = findViewById(R.id.iv_image)
    ivImage.setUrl(url)

### exemple 2)
- anim_id : 다운로드 시 Animation 효과 지정
- default_image_id : Request시 기본 이미지 지정
- fail_image_id : 다운로드 실패 시 이미지 지정
- is_big_size : BigSize 이미지 여부(Default : false = Divice사이즈 보다 작개 Resize) 
- is_resize : ImageView Size에 맞게 Resize

### xml:
    <com.mgkim.mgkimlib.net.widget.WebImageView
      android:id="@+id/iv_image"
      android:layout_width="50dp"
      android:layout_height="50dp"
      app:anim_id="@android:anim/fade_in"
      app:default_image_id="@drawable/ic_default_picture"
      app:fail_image_id="@drawable/ic_frown"
      app:is_big_size="false"
      app:is_resize="true"
      app:layout_constraintLeft_toLeftOf="parent"
      app:layout_constraintRight_toRightOf="parent"
      app:layout_constraintTop_toTopOf="parent" />
### src:
    val = url = "https://via.placeholder.com/600/6dd9cb"
    val ivImage: WebImageView = findViewById(R.id.iv_image)
    ivImage.setUrl(url)    

2.api 요청
-------------
[RequestAPI](https://htmlpreview.github.io/?https://raw.githubusercontent.com/mgkim9/WebImageView/1.0.2/javadocs/webimageview/com.mgkim.libs.webimageview/-request-a-p-i/index.html) (Api 호출을 위한 Request)

[IResultReceiver](https://htmlpreview.github.io/?https://raw.githubusercontent.com/mgkim9/WebImageView/1.0.2/javadocs/webimageview/com.mgkim.libs.webimageview/-i-result-receiver/index.html) (Request 결과를 전달할 listener)

### exemple ) api 요청
https://jsonplaceholder.typicode.com/photos/1

### src:
PhotoDto class

    data class PhotoDto(
        val albumId: Int?,
        val title: String?,
        val id: Int?,
        val url: String?,
        val thumbnailUrl: String?
    )

main class

    val url = "https://jsonplaceholder.typicode.com/photos/1"
    RequestAPI(PhotoDto::class.java, url)
        .setReceiver(object : IResultReceiver<PhotoDto> {
            override fun onResult(isSuccess: Boolean, obj: IRequest<PhotoDto>) {
                // TODO UI Update 
                Log.i(TAG, "RequestAPI isSuccess $isSuccess obj ${obj.getResult()}")
            }
        }).useHandler() //결과(onResult)를 mainThread에서 수행 함 
        .addReq()   // Request 시작

lambda

    RequestAPI(PhotoDto::class.java, url)
        .setReceiver { isSuccess, obj ->
            // TODO UI Update
            Log.i(TAG, "RequestAPI isSuccess $isSuccess obj ${obj.getResult()}")
        }.useHandler() //결과(onResult)를 mainThread에서 수행 함
        .addReq()   // Request 시작


### exemple 2) api 요청 (배열)
https://jsonplaceholder.typicode.com/photos

### src:
main class

    val url = "https://jsonplaceholder.typicode.com/photos"
    RequestAPI(Array<PhotoDto>::class.java, url)
        .setReceiver(object : IResultReceiver<Array<PhotoDto>> {
            override fun onResult(isSuccess: Boolean, obj: IRequest<Array<PhotoDto>>) {
                // TODO UI Update 
                Log.i(TAG, "RequestAPI isSuccess $isSuccess obj ${Arrays.toString(obj.getResult())}")
            }
        }).useHandler() //결과(onResult)를 mainThread에서 수행 함 
        .addReq()   // Request 시작

lambda

    RequestAPI(Array<PhotoDto>::class.java, url)
        .setReceiver { isSuccess, obj ->
            // TODO UI Update
            Log.i(TAG, "RequestAPI isSuccess $isSuccess obj ${Arrays.toString(obj.getResult())}")
        }.useHandler() //결과(onResult)를 mainThread에서 수행 함
        .addReq()   // Request 시작

3.local 작업
-------------
[RequestLocal](https://htmlpreview.github.io/?https://raw.githubusercontent.com/mgkim9/WebImageView/1.0.2/javadocs/webimageview/com.mgkim.libs.webimageview/-request-local/index.html) (Local 작업용 Request)

[IResultReceiver](https://htmlpreview.github.io/?https://raw.githubusercontent.com/mgkim9/WebImageView/1.0.2/javadocs/webimageview/com.mgkim.libs.webimageview/-i-result-receiver/index.html) (Request 결과를 전달할 listener)

[IDoInBackground](https://htmlpreview.github.io/?https://raw.githubusercontent.com/mgkim9/WebImageView/1.0.2/javadocs/webimageview/com.mgkim.libs.webimageview/-i-do-in-background/index.html) (Background 작업 내용을 구현할 interface)

### exemple 1) 기본 Local 작업
### src:
    RequestLocal<Void?>()
    .setDoInBackground(object : IDoInBackground<Void?> {
        override fun doInBackground(): Void? {  // 수행할 local 작업
            // TODO Background
            return null
        }
    }).setReceiver(object : IResultReceiver<Void?> {    // request 결과
        override fun onResult(isSuccess: Boolean, obj: IRequest<Void?>) {
            // TODO UI Update 
        }
    }).useHandler() //결과(onResult)를 mainThread에서 수행 함 
    .addReq()   // Request 시작
    
lambda    
    
    RequestLocal<Void?>().setDoInBackground {
        // TODO Background
        null
    }.setReceiver { isSuccess, obj ->
        // TODO UI Update 
    }.useHandler() //결과(onResult)를 mainThread에서 수행 함
    .addReq()   // Request 시작


2.Global Config
=============
[NetManagerConfig](https://htmlpreview.github.io/?https://raw.githubusercontent.com/mgkim9/WebImageView/1.0.2/javadocs/webimageview/com.mgkim.libs.webimageview/-net-manager-config/index.html) (NetManager 설정 class)

### Config
- diskCacheOption : diskCache 설정 지정
- isMemoryCache : MemoryCache 사용 여부
- preferredConfig : Bitmap.Config 지정
- defaultImageResId : Request시 기본 이미지 지정
- failImageResId : 다운로드 실패 시 이미지 지정
- animResId : 다운로드 시 Animation 효과 지정
- progressResId : 다운로드 시 프로그래스 지정
- isResize : ImageView Size에 맞게 Resize
- isBigSize : BigSize 이미지 여부(Default : false = Divice사이즈 보다 작개 Resize) 

### src:
Application class

    NetManager.init(this, NetManagerConfig(
        NetManagerConfig.WebImageViewConfig(
            diskCacheOption = NetManagerConfig.DiskCacheOption.ALL_DISK_CACEH,
            isMemoryCache = true,
            preferredConfig = Bitmap.Config.ARGB_8888,
            defaultImageResId = R.drawable.ic_default_picture,
            failImageResId = R.drawable.ic_frown,
            animResId = android.R.anim.fade_in,
            progressResId = R.drawable.progress_call,
            isResize = true,
            isBigSize = false
        )
    ))
3.성능
=============
1.vs Glide
-------------
    
### WebImageView(노란색) vs Glide(분홍색)
![Test](https://j.gifs.com/xnzvnq.gif)

- Glide 설정
AppGlideModule class
### src:
    @GlideModule
    class AppGlideModule : AppGlideModule() {
        override fun applyOptions(context: Context, builder: GlideBuilder) {
            super.applyOptions(context, builder)
            val sourceExecutor = newSourceExecutor(3, "source", GlideExecutor.UncaughtThrowableStrategy.DEFAULT)
            val diskExecutor = newDiskCacheExecutor(3, "disk-cache", GlideExecutor.UncaughtThrowableStrategy.DEFAULT)
            builder.setSourceExecutor(sourceExecutor)
                .setDiskCacheExecutor(diskExecutor)
                .setDefaultRequestOptions(
                    RequestOptions().fallback(R.drawable.ic_frown)
                        .placeholder(R.drawable.ic_default_picture)
                )

        }
    }
