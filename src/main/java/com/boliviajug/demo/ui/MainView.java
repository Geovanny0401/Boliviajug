package com.boliviajug.demo.ui;

import com.boliviajug.demo.backend.modelo.Cliente;
import com.boliviajug.demo.backend.repositorio.IClienteRepositorio;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Autowired;


@Route("")
@PWA(name = "BoliviaJug", shortName = "BoliviaJUG")
//@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainView extends Composite<VerticalLayout> {

    private Button refresh = new Button("", VaadinIcon.REFRESH.create());
    private Button add = new Button("", VaadinIcon.PLUS.create());
    private Button edit = new Button("", VaadinIcon.PENCIL.create());

    private final IClienteRepositorio repository;
    private Grid<Cliente> grid = new Grid<>(Cliente.class);

    @Autowired
    public MainView(IClienteRepositorio repository) {
        this.repository=repository;
        initLayout();
        initBehavior();
        refresh();

    }

    private void initLayout() {
        HorizontalLayout header = new HorizontalLayout(refresh, add, edit);
        grid.setColumns("id","nombre","email","sueldo","fechaNacimiento");
        grid.addComponentColumn(user -> new Button("Delete", e -> deleteClicked(user)));
        grid.setSizeFull();
        getContent().add(header, grid);
        getContent().expand(grid);
        getContent().setSizeFull();
        getContent().setMargin(false);
        getContent().setPadding(false);
    }

    private void updateHeader() {
        boolean selected = !grid.asSingleSelect().isEmpty();
        edit.setEnabled(selected);
    }

    private void deleteClicked(Cliente cliente) {
        showRemoveDialog(cliente);
        refresh();
    }

    private void showRemoveDialog(Cliente cliente) {
        RemoveDialog dialog = new RemoveDialog(cliente);
        dialog.open();
    }


    private void initBehavior() {
        grid.asSingleSelect().addValueChangeListener(e -> updateHeader());
        refresh.addClickListener(e -> refresh());
        add.addClickListener(e -> showAddDialog());
        edit.addClickListener(e -> showEditDialog());
    }


    public void refresh() {
        grid.setItems(repository.findAll());
        updateHeader();
    }


    private void showAddDialog() {
        UserFormDialog dialog = new UserFormDialog("Nuevo Cliente", new Cliente());
        dialog.open();
    }

    private void showEditDialog() {
        UserFormDialog dialog = new UserFormDialog("Actualizar Cliente", grid.asSingleSelect().getValue());
        dialog.open();
    }


    private class UserFormDialog extends Dialog {

        private TextField nombre = new TextField("Nombre Completo");
        private EmailField email = new EmailField("Email");
        private BigDecimalField sueldo = new BigDecimalField("Sueldo");
        private DatePicker fechaNacimiento = new DatePicker("Fecha Nacimiento");
        private PasswordField clave = new PasswordField("Clave");

        private Button cancel = new Button("Cancelar");
        private Button save = new Button("Guardar", VaadinIcon.CHECK.create());

        public UserFormDialog(String caption, Cliente cliente) {
            initLayout(caption);
            initBehavior(cliente);
            sueldo.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT);
            sueldo.setPrefixComponent(new Icon(VaadinIcon.DOLLAR));

        }

        private void initLayout(String caption) {
            save.getElement().setAttribute("theme", "primary");
            HorizontalLayout buttons = new HorizontalLayout(cancel, save);
            buttons.setSpacing(true);
            nombre.setRequiredIndicatorVisible(true);
            FormLayout formLayout = new FormLayout(new H2(caption), nombre, email, sueldo, fechaNacimiento, clave);
            VerticalLayout layout = new VerticalLayout(formLayout, buttons);
            layout.setAlignSelf(FlexComponent.Alignment.END, buttons);
            add(layout);
        }

        private void initBehavior(Cliente cliente) {
            BeanValidationBinder<Cliente> binder = new BeanValidationBinder<>(Cliente.class);
            binder.bindInstanceFields(this);
            binder.readBean(cliente);
            cancel.addClickListener(e -> close());
            save.addClickListener(e -> {
                try {
                    binder.validate();
                    binder.writeBean(cliente);
                    repository.save(cliente);
                    close();
                    refresh();
                    Notification.show("Cliente Guardado");
                } catch (ValidationException ex) {
                    Notification.show("Error en el envio de datos");
                }
            });
        }

    }

    //Implementacion Operacion Eliminar
    private class RemoveDialog extends Dialog {
        private Button cancel = new Button("Cancelar");
        private Button delete = new Button("Eliminar", VaadinIcon.TRASH.create());

        public RemoveDialog(Cliente cliente) {
            initLayout(cliente);
            initBehavior(cliente);
        }

        private void initLayout(Cliente cliente) {
            Span span = new Span("Do you really want to delete the user " + cliente.getNombre() +  "?");
            delete.getElement().setAttribute("theme", "error");
            HorizontalLayout buttons = new HorizontalLayout(cancel, delete);
            VerticalLayout layout = new VerticalLayout(new H2("Confirm"), span, buttons);
            layout.setAlignSelf(FlexComponent.Alignment.END, buttons);
            add(layout);
        }

        private void initBehavior(Cliente cliente) {
            cancel.addClickListener(e -> close());
            delete.addClickListener(e -> {
                repository.deleteById(cliente.getId());
                refresh();
                close();
            });
        }
    }
}