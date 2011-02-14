# Filter and Request useful when handling with multipart requests containing files.

Maven dependency:

    <dependency>
        <groupId>pl.softwaremill.common</groupId>
        <artifactId>softwaremill-faces</artifactId>
        <version>${softwaremill-faces-version}</version>
    </dependency>

##HttpMultipartRequest

Most important class in this library is `HttpMultipartRequest`. It is wrapper around
regular `HttpServletRequest`, able to
retrieve regular parameters from multipart request, as well as file ones.
You can wrap `HttpServletRequest` in `HttpMultipartRequest` in servlet, or earlier in
filter:

    if (MultipartFilter.isMultipartRequest(request)) {
      request = new HttpMultipartRequest(request);
    }

You can retrieve regular parameters from HttpMultipartRequest:

    multipartRequest.getParameter("paramName");

Or you can retrieve files:

    multipartRequest.getFileParameter("fileParamName");

Method `getFileParameter` returns `MultipartFile`, which wraps temporary file saved
on disk, or kept in memory. You can get `InputStream` from `MultipartFile` or tmp file.
If `getTmpFile()` is called, file is always saved to disk.

##MultipartFilter

`MultipartFilter` recognises multipart requests and does the wrapping.
In case of JSF, it is necessary to do it in filter, as JSF has to be able to use
request parameters to handle it properly. This are parameters to configure
`MultipartFilter`. None are mandatory, all have default values.

 * `maxContentLength` Default value: 2048000 (2Mb)
 * `maxContentToKeepInMemory` Default value: 1024. If file is less or equal this length,
 it is kept in memory not saved to disk.
 * `onMaxLength` Default value: "abort". Possible values: "abort" or "ignore". What to
 do if file exceeds maxContentLength. If "abort", exception is thrown.
 * `defaultEncoding` Default value: "UTF-8". What encoding to use if browser
 doesn't specify one.
 * `progressListener` Name of class implementing `ProgressListener`.
