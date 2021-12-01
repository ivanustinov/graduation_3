const dishesAjaxUrl = 'admin/dishes/' + restaurant_id + '/';
const lastMenuDateUrl = dishesAjaxUrl + "lastMenuDate";
const lastMenuUrl = dishesAjaxUrl + 'lastMenu';

const ctx = {
    ajaxUrl: dishesAjaxUrl,
    updateTable: function () {
        $.get(this.ajaxUrl + "?date=" + dDate, updateTableByData)
    }
}

function fillDate() {
    let $date = $('#date');
    if ($date.val() === "") {
        $date.val(dDate);
    }
    save("saved");
}

function getLastMenu() {
    ctx.lastMenuTable.ajax.reload();
    // $('#lastMenuTable input:checked').prop('checked', false);
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
        $('#lastMenuModalTitle').append(" " + data);
    });
}


$(function () {
    ctx.datatableApi = $("#datatable").DataTable({
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
        "ajax": {
            "url": dishesAjaxUrl + '?date=' + dDate,
            "dataSrc": ""
        },
        "paging": false,
        "info": true,
        "language": {
            "search": i18n["common.search"]
        },
        "order": [
            [
                0,
                "asc"
            ]
        ]
    });

    ctx.lastMenuTable = $("#lastMenuTable").DataTable({
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
        "bPaginate": false,
        "searching": false,
        "infoEmpty": false,
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

    getLastMenuDate(dDate);

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
});
