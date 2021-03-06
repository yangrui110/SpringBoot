layui.use(['form','layer','laydate','upload'], function(){
    var laydate = layui.laydate;
    var form = layui.form;
    var layer=layui.layer;
    var upload = layui.upload;

    var tableName="entityName-yangrui";

    initSelect();
    function initSelect() {
        selectCompoments-yangrui
    }
    form.on('submit(commit)', function(data){
        console.log(data.field);
        console.log(data.form)
        delete data.field.file;
        var commitData=data.field;
        //进行保存操作
        var commit={
            entityName:tableName,
            data:commitData
        }

        apiClient.insert(commit,function (data) {
            layer.msg("增加成功")
            var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
            parent.layer.close(index); //再执行关闭
            parent.layui.table.reload('data',{
                curr:parent.location.hash.replace('#!fenye=', '')
            })
        });
        return false;
    })
    function initImg(paths){
        if(!paths)
            return ;
        for(var key in paths){
            var elementsByName = document.getElementsByName(key);
            for(var k of elementsByName){
                if(k.tagName=="IMG"){
                    //如果是img标签，设置其src属性
                    k.src=apiClient.getUrl()+"/file/download?path="+paths[key];
                    if(k.parentElement.tagName=='A') {
                        k.parentElement.href = apiClient.getUrl() + "/file/download?path=" + paths[key];
                    }
                }else if(k.tagName=="INPUT"){
                    k.value=paths[key];
                }
            }
        }
    }
    timerCompoments-yangrui

    uploadCompoments-yangrui
});