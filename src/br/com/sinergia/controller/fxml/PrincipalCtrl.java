package br.com.sinergia.controller.fxml;

import br.com.sinergia.functions.CtrlAccMenu;
import br.com.sinergia.functions.CtrlSenhaUsu;
import br.com.sinergia.models.extendeds.PaneMessages;
import br.com.sinergia.models.statics.AppInfo;
import br.com.sinergia.models.statics.Lembrete;
import br.com.sinergia.models.usage.User;
import br.com.sinergia.views.dialogs.ModelDialog;
import br.com.sinergia.views.dialogs.ModelDialogButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ResourceBundle;

import static br.com.sinergia.functions.functions.*;

public class PrincipalCtrl implements Initializable {

    CtrlAccMenu ctrlAccMenu;

    @FXML
    private SplitPane PaneFundo;
    @FXML
    private Accordion accMenus;
    @FXML
    private TabPane AbaPane;
    @FXML
    private VBox VBoxFavoritos, VBoxRecentes;
    @FXML
    private TitledPane TtpMensagens, TtpPropriedades;
    @FXML
    private Button BtnSalvarLemb, BtnCancelarLemb, BtnMensagens, BtnPropriedades, BtnAttSenha, BtnPreferencias, BtnDeslogar, BtnSair;
    @FXML
    private JFXTextField TxtPesqTela;
    @FXML
    private HTMLEditor TxtLembrete;
    @FXML
    private ImageView ImgSearchFrame, ImgUsu;
    @FXML
    private Label LblInfoFaixa, LblNomeLembrete, LblDHLembrete, LblDataLogin, LblHoraLogin, LblVersaoExec, LblVersaoBD, LblCodSessao, LblPerfil;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        estrutura();
        appEvents();
    }

    private void estrutura() {
        AppInfo.setMainTabPane(AbaPane);
        AppInfo.setvBoxFavoritos(VBoxFavoritos);
        AppInfo.setvBoxRecentes(VBoxRecentes);
        AppInfo.setBtnMensagens(BtnMensagens);
        LblInfoFaixa.setText("Usuário: " + User.getCurrent().getCodUsu() + " - " + User.getCurrent().getNomeUsu() +
                " x Empresa: " + User.getCurrent().getCodEmp() + " - " + User.getCurrent().getNomeFantasiaEmp());
        LblNomeLembrete.setText("Olá, " + User.getCurrent().getNomeUsu() + "!");
        ctrlAccMenu = new CtrlAccMenu(accMenus);
        ImgUsu.setFitWidth(90);
        ImgUsu.setFitHeight(90);
        ImgUsu.setImage(User.getCurrent().getFotoUsu());
        LblDataLogin.setText(formatDate(dataFormater, User.getCurrent().getDhLogin()));
        LblHoraLogin.setText(formatDate(horaFormater, User.getCurrent().getDhLogin()));
        LblVersaoExec.setText("Versão Exec: " + AppInfo.getVersaoExec());
        LblVersaoBD.setText("Versão BD: " + AppInfo.getVersaoDB());
        LblCodSessao.setText("Cód. Sessão: " + User.getCurrent().getCodSessão());
        LblPerfil.setText("Cód. Perfil: " + User.getCurrent().getCodPerfil());
        TxtLembrete.setHtmlText(nvl(Lembrete.getUserLembrete().getLembrete()));
        LblDHLembrete.setText("Data/Hora Ult. Alteração: " + formatDate(dataHoraFormater, Lembrete.getUserLembrete().getDhLembrete()));
        Platform.runLater(() -> TtpMensagens.setContent(new PaneMessages(TtpMensagens)));
    }

    private void appEvents() {
        Platform.runLater(() -> {
            TtpPropriedades.setExpanded(false);
            BtnPropriedades.setOnAction(evt -> {
                TtpPropriedades.setExpanded(true);
                TtpPropriedades.requestFocus();
            });
            TtpPropriedades.focusedProperty().addListener((obs, wasF, isF) -> {
                if (wasF) {
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(150), //0.150clara Segundos
                            ae -> TtpPropriedades.setExpanded(false)));
                    timeline.play();
                }
            });
            TtpPropriedades.expandedProperty().addListener((obs, wasE, isE) -> {
                if (isE) TtpPropriedades.setVisible(true);
                else if (wasE) TtpPropriedades.setVisible(false);
            });
            TtpMensagens.setExpanded(false);
            BtnMensagens.setOnAction(evt -> {
                TtpMensagens.setExpanded(true);
                TtpMensagens.requestFocus();
            });
            TtpMensagens.expandedProperty().addListener((obs, wasE, isE) -> {
                if (isE) TtpMensagens.setVisible(true);
                else if (wasE) TtpMensagens.setVisible(false);
            });
            TxtPesqTela.setOnAction(e -> ctrlAccMenu.searchFrame(TxtPesqTela.getText()));
            ImgSearchFrame.setOnMouseClicked(e -> ctrlAccMenu.searchFrame(TxtPesqTela.getText()));
            BtnAttSenha.setOnAction(e -> new CtrlSenhaUsu(User.getCurrent().getCodUsu(), User.getCurrent().getSenhaUsu(), false));
            BtnDeslogar.setOnAction(e -> {
                int resp = ModelDialogButton.YesNoDialog(this.getClass(), null,
                        "Deseja realmente deslogar do sistema?");
                if (resp == 1) {
                    AppInfo.getStageMain().close();
                    main mi = new main();
                    mi.start(new Stage());
                }
            });
            BtnSair.setOnAction(e -> {
                int resp = ModelDialogButton.YesNoDialog(this.getClass(), null,
                        "Deseja realmente sair do sistema?");
                if (resp == 1) AppInfo.getStageMain().close();
            });
            BtnSalvarLemb.setOnAction(e -> {
                if (TxtLembrete.getHtmlText().equals(Lembrete.getUserLembrete().getLembrete())) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Não há alterações no lembrete para salvar."));
                    ModelDialog.getDialog().raise();
                } else {
                    int resp = ModelDialogButton.YesNoDialog(this.getClass(), null, "Deseja realmente salvar o lembrete?");
                    if (resp == 1) {
                        Lembrete.setUserLembrete(new Lembrete(User.getCurrent().getCodUsu(),
                                Timestamp.from(Instant.now()),
                                TxtLembrete.getHtmlText()));
                        if (!Lembrete.getUserLembrete().getLembrete().equals(TxtLembrete.getHtmlText())) {
                            //Verificamos isso para certificar que não houve erros na inserção
                            LblDHLembrete.setText("Data/Hora Ult. Alteração: " + formatDate(dataHoraFormater, Lembrete.getUserLembrete().getDhLembrete()));
                        }
                    }
                }
            });
            BtnCancelarLemb.setOnAction(e -> {
                if (TxtLembrete.getHtmlText().equals(Lembrete.getUserLembrete().getLembrete())) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Não há alterações no lembrete para retormar."));
                    ModelDialog.getDialog().raise();
                } else {
                    int resp = ModelDialogButton.YesNoDialog(this.getClass(), null,
                            "Deseja retomar o lembrete?\n" +
                                    "Todas as alterações serão perdidas.");
                    if (resp == 1) {
                        TxtLembrete.setHtmlText(nvl(Lembrete.getUserLembrete().getLembrete()));
                    }
                }
            });
        });
    }
}
