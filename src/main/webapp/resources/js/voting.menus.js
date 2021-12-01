const rootDishesAjaxUrl = "dishes/";
const dishesAjaxUrl = "admin/dishes/";
const restaurantWithoutDishes = "admin/restaurants/without";

let form;

function remove(restaurant_id, date) {
    $.ajax({
        url: dishesAjaxUrl + restaurant_id,
        type: "DELETE",
        data: {
            date: date
        }
    }).done(function () {
        $("#" + restaurant_id).detach();
        successNoty("deleted");
    });
}

function add(date) {
    getRestaurantsWithoutDishes(date);
    $("#modalTitle").html(i18n["addTitle"]);
    $("#select_restaurant").modal();
}

function getRestaurantsWithoutDishes(date) {
    $.ajax({
        url: restaurantWithoutDishes,
        type: "GET",
        data: {
            date: date
        }
    }).done(function (data) {
        $('#name>option').detach();
        $.each(data, function (index, value) {
            $('#name').append($('<option>', {value: value.id, html: value.name}));
        });
        let value1 = $('#name option:selected').attr('value');
        $('#detailsForm').attr('action', rootDishesAjaxUrl + value1 +'/' + date)
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

$(function () {
    $('#name').on('change', function () {
        let value1 = $('#name option:selected').val();
        $('#detailsForm').attr('action', rootDishesAjaxUrl + value1 + '/' + dDate)
    });
    //form = $('#detailsForm');
    // $('#create_button').click(function () {
    //     $('#detailsForm').submit();
    // });
    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});