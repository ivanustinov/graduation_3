let form;

function makeEditable(datatableOpts) {
    let url = "";
    if (localeCode === "ru") {
        url =  "//cdn.datatables.net/plug-ins/1.11.3/i18n/ru.json";
    }
    ctx.datatableApi = $("#datatable").DataTable(
        // https://api.jquery.com/jquery.extend/#jQuery-extend-deep-target-object1-objectN
        $.extend(true, datatableOpts,
            {
                "ajax": {
                    "url": ctx.ajaxUrl,
                    "dataSrc": ""
                },
                "language": {
                    "url": url
                }
            }
        ));
    form = $('#detailsForm');
    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    });

    // solve problem with cache in IE: https://stackoverflow.com/a/4303862/548473
    $.ajaxSetup({cache: false});

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
}

function add() {
    $("#modalTitle").html(i18n["addTitle"]);
    form.find(":input").val("");
    $("#editRow").modal();
}

function updateRow(id) {
    form.find(":input").val("");
    $("#modalTitle").html(i18n["editTitle"]);
    $.get(ctx.ajaxUrl + id, function (data) {
        $.each(data, function (key, value) {
            form.find("input[name='" + key + "']").val(value);
        });
        $('#editRow').modal();
    });
}

function deleteRow(id) {
    if (confirm(i18n['common.confirm'])) {
        $.ajax({
            url: ctx.ajaxUrl + id,
            type: "DELETE"
        }).done(function () {
            ctx.updateTable();
            successNoty("deleted");
        });
    }
}

function updateTableByData(data) {
    ctx.datatableApi.clear().rows.add(data).draw();
}

function save(key) {
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl,
        data: form.serialize()
    }).done(function () {
        $("#editRow").modal("hide");
        ctx.updateTable();
        if (!key) {
            key = "common.saved"
        }
        successNoty(key);
    });
}

let failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(key) {
    closeNoty();
    new Noty({
        text: "<span class='fa fa-lg fa-check'></span> &nbsp;" + i18n[key],
        type: 'success',
        layout: "bottomRight",
        timeout: 1000
    }).show();
}

function renderEditBtn(data, type, row) {
    if (type === "display") {
        return "<a onclick='updateRow(" + row.id + ");'><span class='fa fa-pencil'></span></a>";
    }
}

function renderDeleteBtn(data, type, row) {
    if (type === "display") {
        return "<a onclick='deleteRow(" + row.id + ");'><span class='fa fa-remove'></span></a>";
    }
}

function failNoty(jqXHR) {
    closeNoty();
    var errorInfo = jqXHR.responseJSON;
    failedNote = new Noty({
        text: "<span class='fa fa-lg fa-exclamation-circle'></span> &nbsp;" + errorInfo.message.join("<br>"),
        type: "error",
        layout: "bottomRight"
    });
    failedNote.show()
}