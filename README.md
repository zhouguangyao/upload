import resumable project server example.  
========== application.yml demo start ==========  
upload:  
  dir: 'D:\\upload_dir'//上传文件存放目录    
  chunkSize: 30000000 //单片大小  
  simultaneousUploads: 5 //每次提交片数  
  testChunks: false//上前文件块是否先通过get方式发送文件信息检测文件是否已经上传  
========== application.yml demo end ==========  

========== Controller demo start ==========  
    @Autowired  
    private Resumable resumable;   
    @PostMapping(value = "",name="上传文件")  
    public Map<String, Object> upload(HttpServletRequest request) {  
        Map<String, Object> map = new HashMap<>();  
        log.info("resumable 上传文件 start ==> ");  
        resumable.upload(request);  
        log.info("resumable 上传文件 end ==> ");  
        Map responseMap = new HashMap();  
        responseMap.put("code", "500");  
        return responseMap;  
    }  
========== Controller demo end ==========  

