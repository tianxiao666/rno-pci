$(function() {
        var f_name = window.parent.document.getElementById("userName").textContent;


        $.ajax({
            type: 'POST',
            contentType:"application/json;charset=utf-8",
            url: 'receiveUserName',
            data: JSON.stringify({'account': f_name}),
            dataType: 'json',
            success: function (result) {
                if (result.statusCode == 200) {
                    alert('保存成功！');
                }
                else {
                    alert('保存失败！');
                }
            }

        })

});