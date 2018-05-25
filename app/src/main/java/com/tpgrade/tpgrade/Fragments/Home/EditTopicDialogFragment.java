package com.tpgrade.tpgrade.Fragments.Home;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.tpgrade.models.Topic;
import com.tpgrade.tpgrade.R;


public class EditTopicDialogFragment extends DialogFragment {

    private Topic currentTopic;

    public void setCurrentTopic(Topic currentTopic) {
        this.currentTopic = currentTopic;
    }


    // 1. Defines the listener interface with a method passing back data result.
    public interface EditTopicDialogListener {
        void onFinishEditTopicDialog(Topic topic);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_edit_topic, null);

        final EditText txtTestName = (EditText) dialogView.findViewById(R.id.txtTestName);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                // Add action buttons
                .setPositiveButton(R.string.n_frag_add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String testName = txtTestName.getText().toString().trim();

                        Topic topic = Topic.findById(Topic.class, currentTopic.getId());
                        topic.testName = testName;
                        topic.save();

                        currentTopic.testName = testName;

                        EditTopicDialogListener listener = (EditTopicDialogListener) getActivity();
                        listener.onFinishEditTopicDialog(currentTopic);

                        Toast toast = Toast.makeText(getActivity(), getString(R.string.home_message__success_update), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                })
                .setNegativeButton(R.string.n_frag_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditTopicDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
