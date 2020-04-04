package pl.szczodrzynski.edziennik.ui.modules.messages;


import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.szczodrzynski.edziennik.App;
import pl.szczodrzynski.edziennik.MainActivity;
import pl.szczodrzynski.edziennik.R;
import pl.szczodrzynski.edziennik.data.db.entity.Message;
import pl.szczodrzynski.edziennik.data.db.full.MessageFull;
import pl.szczodrzynski.edziennik.data.db.full.MessageRecipientFull;
import pl.szczodrzynski.edziennik.databinding.MessagesListBinding;
import pl.szczodrzynski.edziennik.ui.modules.base.lazypager.LazyFragment;
import pl.szczodrzynski.edziennik.utils.SimpleDividerItemDecoration;
import pl.szczodrzynski.edziennik.utils.Themes;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static pl.szczodrzynski.edziennik.utils.Utils.d;

public class MessagesListFragmentOld extends LazyFragment {

    private App app = null;
    private MainActivity activity = null;
    private MessagesListBinding b = null;

    private Rect viewRect = new Rect();
    private MessagesAdapter messagesAdapter = null;
    private ViewGroup viewParent = null;

    static final Interpolator transitionInterpolator = new FastOutSlowInInterpolator();
    static final long TRANSITION_DURATION = 300L;
    static final String TAP_POSITION = "tap_position";

    public static int[] tapPositions = {NO_POSITION, NO_POSITION};
    public static int[] topPositions = {NO_POSITION, NO_POSITION};
    public static int[] bottomPositions = {NO_POSITION, NO_POSITION};

    private int messageType = Message.TYPE_RECEIVED;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        if (getActivity() == null || getContext() == null)
            return null;
        app = (App) activity.getApplication();
        getContext().getTheme().applyStyle(Themes.INSTANCE.getAppTheme(), true);
        // activity, context and profile is valid
        b = DataBindingUtil.inflate(inflater, R.layout.messages_list, container, false);
        return b.getRoot();
    }

    @Override
    public boolean onPageCreated() {
        if (app == null || activity == null || b == null || !isAdded())
            return false;

        long messageId = -1;
        if (getArguments() != null) {
            messageId = getArguments().getLong("messageId", -1);
        }
        if (messageId != -1) {
            Bundle args = new Bundle();
            args.putLong("messageId", messageId);
            getArguments().remove("messageId");
            activity.loadTarget(MainActivity.TARGET_MESSAGES_DETAILS, args);
            return false;
        }

        if (getArguments() != null) {
            messageType = getArguments().getInt("messageType", Message.TYPE_RECEIVED);
        }

        /*b.refreshLayout.setOnRefreshListener(() -> {
            activity.syncCurrentFeature(messageType, b.refreshLayout);
        });*/

        /*messagesAdapter = new MessagesAdapter(app, ((parent, view1, position, id) -> {
            // TODO ANIMATION
            tapPositions[messageType] = position;
            topPositions[messageType] = ((LinearLayoutManager) b.emailList.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            bottomPositions[messageType] = ((LinearLayoutManager) b.emailList.getLayoutManager()).findLastCompletelyVisibleItemPosition();

            *//*view1.getGlobalVisibleRect(viewRect);
            ((Transition) MessagesListFragment.this.getExitTransition()).setEpicenterCallback(new Transition.EpicenterCallback() {
                @Override
                public Rect onGetEpicenter(@NonNull Transition transition) {
                    return viewRect;
                }
            });*//*

            Bundle args = new Bundle();
            args.putLong("messageId", messagesAdapter.getMessageList().get(position).id);
            activity.loadTarget(MainActivity.TARGET_MESSAGES_DETAILS, args);

            // KOD W WERSJI 2.7
            // TODO ANIMATION
            *//*TransitionSet sharedElementTransition = new TransitionSet()
                    .addTransition(new Fade())
                    .addTransition(new ChangeBounds())
                    .addTransition(new ChangeTransform())
                    .addTransition(new ChangeImageTransform())
                    .setDuration(TRANSITION_DURATION)
                    .setInterpolator(transitionInterpolator);

            MessagesDetailsFragment fragment = new MessagesDetailsFragment();
            Bundle args = new Bundle();
            args.putLong("messageId", messagesAdapter.messageList.get(position).id);
            fragment.setArguments(args);
            fragment.setSharedElementEnterTransition(sharedElementTransition);
            fragment.setSharedElementReturnTransition(sharedElementTransition);*//*

            // JAKIS STARSZY KOD
            *//*Intent intent = new Intent(activity, MessagesDetailsActivity.class);
            intent.putExtra("item_id", 1);
            intent.putExtra("transition_name", ViewCompat.getTransitionName(view1));


            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    view1,
                    getString(R.string.transition_name)
            );

            TransitionManager.beginDelayedTransition((ViewGroup) view1, sharedElementTransition);
            setEnterTransition(sharedElementTransition);
            setReturnTransition(sharedElementTransition);
            setExitTransition(sharedElementTransition);
            setSharedElementEnterTransition(sharedElementTransition);
            setSharedElementReturnTransition(sharedElementTransition);
            startActivity(intent, options.toBundle());*//*

            *//*activity.getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .addSharedElement(view1, getString(R.string.transition_name))
                    .commit();*//*

        }));*/


        //tapPosition = savedInstanceState != null ? savedInstanceState.getInt(TAP_POSITION, tapPosition) : tapPosition;

        // May not be the best place to postpone transition. Just an example to demo how reenter transition works.
         // TODO ANIMATION
        //postponeEnterTransition();

        viewParent = (ViewGroup) getView().getParent();

        b.emailList.setLayoutManager(new LinearLayoutManager(getView().getContext()));
        b.emailList.addItemDecoration(new SimpleDividerItemDecoration(getView().getContext()));
        b.emailList.setAdapter(messagesAdapter);
        b.emailList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (b.emailList.canScrollVertically(-1)) {
                    setSwipeToRefresh(false);
                }
                if (!b.emailList.canScrollVertically(-1) && newState == SCROLL_STATE_IDLE) {
                    setSwipeToRefresh(true);
                }
            }
        });

        if (messageType == Message.TYPE_RECEIVED) {
            App.db.messageDao().getReceived(App.Companion.getProfileId()).observe(this, messageFulls -> {
                createMessageList(messageFulls);
            });
        }
        else if (messageType == Message.TYPE_DELETED) {
            App.db.messageDao().getDeleted(App.Companion.getProfileId()).observe(this, messageFulls -> {
                createMessageList(messageFulls);
            });
        }
        else if (messageType == Message.TYPE_SENT) {
            App.db.messageDao().getSent(App.Companion.getProfileId()).observe(this, messageFulls -> {
                AsyncTask.execute(() -> {
                    List<MessageRecipientFull> messageRecipients = App.db.messageRecipientDao().getAll(App.Companion.getProfileId());
                    List<Long> messageIds = new ArrayList<>();
                    for (MessageFull messageFull: messageFulls) {
                        messageIds.add(messageFull.getId());
                    }
                    for (MessageRecipientFull messageRecipientFull: messageRecipients) {
                        if (messageRecipientFull.id == -1)
                            continue;

                        int index = -1;

                        int i = -1;
                        for (long id: messageIds) {
                            //index++;
                            i++;
                            if (id == messageRecipientFull.messageId) {
                                index = i;
                                break;
                            }
                        }

                        if (index >= 0) {
                            MessageFull messageFull = messageFulls.get(index);
                            if (messageFull != null) {
                                messageFull.addRecipient(messageRecipientFull);
                            }
                        }
                    }
                    activity.runOnUiThread(() -> {
                        createMessageList(messageFulls);
                    });
                });
            });
        }

        return true;
    }

    private void createMessageList(List<MessageFull> messageFulls) {
        b.progressBar.setVisibility(View.GONE);
        b.emailList.setVisibility(View.VISIBLE);
        //messagesAdapter.setData(messageFulls);

        LinearLayoutManager layoutManager = (LinearLayoutManager) b.emailList.getLayoutManager();
        if (tapPositions[messageType] != NO_POSITION && layoutManager != null) {
            //d("MessageList", "Scrolling");

            if (topPositions[messageType] > layoutManager.findLastCompletelyVisibleItemPosition()) {
                b.emailList.scrollToPosition(topPositions[messageType]);
            }
            else if (bottomPositions[messageType] < layoutManager.findFirstCompletelyVisibleItemPosition()) {
                b.emailList.scrollToPosition(bottomPositions[messageType]);
            }
            else {
                b.emailList.scrollToPosition(tapPositions[messageType]);
            }

            //tapPositions[messageType] = NO_POSITION;
            //topPositions[messageType] = NO_POSITION;
            //bottomPositions[messageType] = NO_POSITION;
        }
        // TODO ANIMATION
        /*final ViewTreeObserver observer = viewParent.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                *//*viewParent.getViewTreeObserver().removeOnPreDrawListener(this);
                if (getExitTransition() == null) {
                    setExitTransition(new SlideExplode().setDuration(TRANSITION_DURATION).setInterpolator(transitionInterpolator));
                }

                View view2 = layoutManager != null ? layoutManager.findViewByPosition(tapPosition) : null;
                if (view2 != null) {
                    view2.getGlobalVisibleRect(viewRect);
                    ((Transition) getExitTransition()).setEpicenterCallback(new Transition.EpicenterCallback() {
                        @Override
                        public Rect onGetEpicenter(@NonNull Transition transition) {
                            return viewRect;
                        }
                    });
                }

                d("MessagesList", "topPosition "+topPosition);
                d("MessagesList", "tapPosition "+tapPosition);
                d("MessagesList", "bottomPosition "+bottomPosition);*//*


                startPostponedEnterTransition();
                return true;
            }
        });*/
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        d("MessagesList", "onSaveInstanceState position "+tapPositions[messageType]);
        outState.putInt(TAP_POSITION, tapPositions[messageType]);
    }
}