const dishesAjaxUrl = 'admin/dishes/' + restaurant_id + '/';
const lastMenuDateUrl = dishesAjaxUrl + "lastMenuDate";
const lastMenuUrl = dishesAjaxUrl + 'lastMenu';

let lastMenuModalTitle;
const ctx = {
    ajaxUrl: dishesAjaxUrl + "?date=" + dDate,
    updateTable: function () {
        $.get(this.ajaxUrl, updateTableByData)
    }
}

function save(key) {
    $.ajax({
        type: "POST",
        url: dishesAjaxUrl,
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

function deleteRow(id) {
    if (confirm(i18n['common.confirm'])) {
        $.ajax({
            url: dishesAjaxUrl + id,
            type: "DELETE"
        }).done(function () {
            ctx.updateTable();
            successNoty("deleted");
        });
    }
}

function updateRow(id) {
    form.find(":input").val("");
    $("#modalTitle").html(i18n["editTitle"]);
    $.get(dishesAjaxUrl + id, function (data) {
        $.each(data, function (key, value) {
            form.find("input[name='" + key + "']").val(value);
        });
        $('#editRow').modal();
    });
}

function fillDate() {
    let $date = $('#date');
    if ($date.val() === "") {
        $date.val(dDate);
    }
    save("saved");
}

function getLastMenu() {
    if (ctx.lastMenuTable === undefined) {
        ctx.lastMenuTable = createLastMenuTable();
    } else {
        ctx.lastMenuTable.ajax.reload();
    }
    getLastMenuDate(dDate);
    $('#lastMenu').modal();
}

function setNewDateInLastMenu() {
    let dishSelected = ctx.lastMenuTable.rows($('tr').has('input:checked')).data().toArray();
    $.each(dishSelected, function (index, row) {
        row.date = dDate;
        row.id = null;
    });
    return dishSelected;
}

function sentDishes() {
    let newDateInLastMenu = setNewDateInLastMenu();
    let length = newDateInLastMenu.length;
    $.post({
            url: lastMenuUrl,
            data: JSON.stringify(newDateInLastMenu),
            contentType: "application/json; charset=utf-8"
        }
    ).done(function () {
        $('#lastMenu').modal("hide");
        ctx.updateTable();
        if (length === 1) {
            successNoty("common.saved");
        } else if (length > 1) {
            successNoty("commons.saved");
        }
    });
}

function getLastMenuDate(date) {
    $.ajax({
        url: lastMenuDateUrl,
        type: "GET",
        data: {
            date: date
        }
    }).done(function (data) {
        $('#lastMenuModalTitle').html(lastMenuModalTitle + " " + data);
    });
}

function createLastMenuTable() {
    let url = "";
    if (localeCode === "ru") {
        url = "resources/js/plug-ins/ru.json";
    }
    return $("#lastMenuTable").DataTable({
        "columns": [
            {
                "data": "name"
            },
            {
                "data": "price",
            },
            {
                "render": function () {
                    return "<input type='checkbox'/>";
                }
            },
        ],
        "language": {
            "url": url
        },
        "bPaginate": false,
        "searching": false,
        "infoEmpty": false,
        "info": false,
        "ajax": {
            "url": lastMenuUrl + '?date=' + dDate,
            "dataSrc": ""
        },
        "order": [
            [
                0,
                "asc"
            ]
        ]
    });
}


$(function () {
    makeEditable({
        "columns": [
            {
                "data": "name"
            },
            {
                "data": "price",
            },
            {
                "orderable": false,
                "defaultContent": "",
                "render": renderEditBtn
            },
            {
                "orderable": false,
                "defaultContent": "",
                "render": renderDeleteBtn
            }
        ],
        "bPaginate": false,
        "searching": false,
        "infoEmpty": false,
        "info": false,
        "order": [
            [
                0,
                "asc"
            ]
        ]
    });
    lastMenuModalTitle = $('#lastMenuModalTitle').html();
});
