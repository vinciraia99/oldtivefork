(function() {
	// Fetches footer from page
	EditorUi.prototype.createFooter = function() {
		var footer = this.createDiv('geFooter');
		var contents = document.getElementById('geFooter');

		if (contents != null) {
			footer.appendChild(contents);
			contents.style.visibility = 'visible';
		}

		return footer;
	};

	// Overrides footer height
	EditorUi.prototype.footerHeight = 44;

	// Initializes the user interface
	var editorUiInit = EditorUi.prototype.init;
	editorUiGlobal = editorUiInit;
	EditorUi.prototype.init = function() {
		editorUiInit.apply(this, arguments);
		var signs = this.sidebar.signs;
		var mockups = this.sidebar.mockups;
		var ee = this.sidebar.ee;
		var pids = this.sidebar.pids;
		var cisco = this.sidebar.cisco;

		// Adds style input in test mode
		if (urlParams['test'] == '1') {
			var footer = document.getElementById('geFooter');

			if (footer != null) {
				this.styleInput = document.createElement('input');
				this.styleInput.setAttribute('type', 'text');
				this.styleInput.style.position = 'absolute';
				this.styleInput.style.left = '2px';
				// Workaround for ignore right CSS property in FF
				this.styleInput.style.width = '98%';
				this.styleInput.style.visibility = 'hidden';

				mxEvent.addListener(this.styleInput, 'change', mxUtils.bind(
						this, function() {
							this.editor.graph.getModel().setStyle(
									this.editor.graph.getSelectionCell(),
									this.styleInput.value);
						}));

				footer.appendChild(this.styleInput);

				this.editor.graph
						.getSelectionModel()
						.addListener(
								mxEvent.CHANGE,
								mxUtils
										.bind(
												this,
												function(sender, evt) {
													if (this.editor.graph
															.getSelectionCount() > 0) {
														var cell = this.editor.graph
																.getSelectionCell();
														var style = this.editor.graph
																.getModel()
																.getStyle(cell);

														this.styleInput.value = style;
														this.styleInput.style.visibility = 'visible';
													} else {
														this.styleInput.style.visibility = 'hidden';
													}
												}));
			}

			var isSelectionAllowed = this.isSelectionAllowed;
			this.isSelectionAllowed = function(evt) {
				if (mxEvent.getSource(evt) == this.styleInput) {
					return true;
				}

				return isSelectionAllowed.apply(this, arguments);
			};
		}

		// Changes default extension for Google Drive
		this.editor.getOrCreateFilename = function() {
			return this.filename || mxResources.get('drawing', [ counter ]);
		};

		// Removes info text in page
		var info = document.getElementById('geInfo');

		if (info != null) {
			info.parentNode.removeChild(info);
		}

		// Hides libraries
		var stc = urlParams['libs'];

		// Default libraries for domains
		if (stc == null) {
			stc = 'general;images;uml;ios;er;bpmn;flowchart;basic;arrows;mockups';
		}

		var tmp = stc.split(';');

		// Individual libs
		var all = [ 'general', 'images', 'uml', 'er', 'ios', 'flowchart',
				'basic', 'arrows', 'leanMapping' ]

		for (var i = 0; i < all.length; i++) {
			if (mxUtils.indexOf(tmp, all[i]) < 0) {
				this.sidebar.togglePalettes('', [ all[i] ]);
			}
		}

		if (mxUtils.indexOf(tmp, 'bpmn') < 0) {
			this.sidebar.togglePalettes('', [ 'bpmn', 'bpmnGateways',
					'bpmnEvents' ]);
		}

		if (mxUtils.indexOf(tmp, 'clipart') < 0) {
			this.sidebar.togglePalettes('', [ 'computer', 'finance', 'clipart',
					'networking', 'people', 'telco' ]);
		}

		if (mxUtils.indexOf(tmp, 'mockups') < 0) {
			this.sidebar.togglePalettes('mockup', mockups);
		}

		if (mxUtils.indexOf(tmp, 'signs') < 0) {
			this.sidebar.togglePalettes('signs', signs);
		}

		if (mxUtils.indexOf(tmp, 'electrical') < 0) {
			this.sidebar.togglePalettes('electrical', ee);
		}

		if (mxUtils.indexOf(tmp, 'aws') < 0) {
			this.sidebar.togglePalettes('aws', [ 'Compute', 'ContentDelivery',
					'Database', 'DeploymentManagement', 'Groups', 'Messaging',
					'Misc', 'Networking', 'NonServiceSpecific',
					'OnDemandWorkforce', 'Storage' ]);
		}

		if (mxUtils.indexOf(tmp, 'pid') < 0) {
			this.sidebar.togglePalettes('pid', pids);
		}

		if (mxUtils.indexOf(tmp, 'cisco') < 0) {
			this.sidebar.togglePalettes('cisco', cisco);
		}
		// TODO: Expand the first entry

		// Adds zoom via shift-wheel
		mxEvent
				.addMouseWheelListener(mxUtils
						.bind(
								this,
								function(evt, up) {
									var outlineWheel = false;

									if (this.editor.outline.outline.dialect == mxConstants.DIALECT_SVG) {
										var source = mxEvent.getSource(evt);

										while (source != null) {
											if (source == this.editor.outline.outline.view.canvas.ownerSVGElement) {
												outlineWheel = true;
												break;
											}

											source = source.parentNode;
										}
									}

									if (mxEvent.isShiftDown(evt)
											|| outlineWheel) {
										if (up) {
											this.editor.graph.zoomIn();
										} else {
											this.editor.graph.zoomOut();
										}

										mxEvent.consume(evt);
									}
								}));

		// Installs popup menu in Sidebar
		var menu = new mxPopupMenu(this.menus.get('moreShapes').funct);
		var ignoreEvent = false;

		mxEvent.addListener(this.sidebarContainer, 'mousedown', function(evt) {
			if (!ignoreEvent) {
				menu.hideMenu();
			}

			if (!mxEvent.isConsumed(evt) && mxEvent.isPopupTrigger(evt)) {
				var origin = mxUtils.getScrollOrigin();
				var point = new mxPoint(mxEvent.getClientX(evt) + origin.x,
						mxEvent.getClientY(evt) + origin.y);

				// Menu is shifted by 1 pixel so that the mouse up event
				// is routed via the underlying shape instead of the DIV
				menu.popup(point.x + 1, point.y + 1, null, evt);
				mxEvent.consume(evt, false);
				ignoreEvent = true;
			}
		});

		mxEvent
				.addListener(
						this.sidebar.moreShapes,
						'mousedown',
						function(evt) {
							menu.hideMenu();

							if (!mxEvent.isConsumed(evt)) {
								var origin = mxUtils.getScrollOrigin();
								var point = new mxPoint(mxEvent.getClientX(evt)
										+ origin.x, mxEvent.getClientY(evt)
										+ origin.y);
								// Menu is shifted by 1 pixel so that the mouse
								// up event
								// is routed via the underlying shape instead of
								// the DIV
								menu.popup(point.x + 1, point.y + 1, null, evt);
								mxEvent.consume(evt, false);
								ignoreEvent = true;
							}
						});

		// NOTE: In quirks mode the event is not the same instance as above
		mxEvent.addListener(document.body, 'mousedown', function(evt) {
			if (!ignoreEvent) {
				menu.hideMenu();
			}

			ignoreEvent = false;
		});

		// Disables crisp rendering in SVG except for connectors, actors,
		// cylinder,
		// ellipses must be enabled after rendering the sidebar items
		if (urlParams['aa'] == '0') {
			mxShape.prototype.crisp = false;
			mxCellRenderer.prototype.defaultShapes['folder'].prototype.crisp = false;
		}

		// Initial page layout view, scrollBuffer and timer-based scrolling
		var graph = this.editor.graph;
		var pageBorder = 800;
		graph.timerAutoScroll = true;

		var graphSizeDidChange = graph.sizeDidChange;
		graph.sizeDidChange = function() {
			var bounds = this.getGraphBounds();

			if (this.container != null) {
				if (this.scrollbars && !touchStyle) {
					var border = this.getBorder();

					var t = this.view.translate;
					var s = this.view.scale;
					var width = Math.max(0, bounds.x + bounds.width + border
							- t.x * s);
					var height = Math.max(0, bounds.y + bounds.height + border
							- t.y * s);
					var fmt = this.pageFormat;
					var ps = this.pageScale;
					var page = new mxRectangle(0, 0, fmt.width * ps, fmt.height
							* ps);

					var hCount = (this.pageBreaksVisible) ? Math.max(1, Math
							.ceil(width / (page.width * s))) : 1;
					var vCount = (this.pageBreaksVisible) ? Math.max(1, Math
							.ceil(height / (page.height * s))) : 1;

					// Computes unscaled, untranslated graph bounds
					var x = (bounds.width > 0) ? bounds.x / this.view.scale
							- this.view.translate.x : 0;
					var y = (bounds.height > 0) ? bounds.y / this.view.scale
							- this.view.translate.y : 0;
					var w = bounds.width / this.view.scale;
					var h = bounds.height / this.view.scale;

					var fmt = this.pageFormat;
					var ps = this.pageScale;

					var pw = fmt.width * ps;
					var ph = fmt.height * ps;

					var x0 = Math.floor(Math.min(0, x) / pw);
					var y0 = Math.floor(Math.min(0, y) / ph);

					hCount -= x0;
					vCount -= y0;

					// Extends the page border based on current scale
					var pb = pageBorder;

					var minWidth = (pb * 2 + pw * hCount);
					var minHeight = (pb * 2 + ph * vCount);
					var m = graph.minimumGraphSize;

					if (m == null || m.width != minWidth
							|| m.height != minHeight) {
						graph.minimumGraphSize = new mxRectangle(0, 0,
								minWidth, minHeight);
					}

					var autoDx = pb - x0 * fmt.width;
					var autoDy = pb - y0 * fmt.height;

					if (!this.autoTranslate
							&& (graph.view.translate.x != autoDx || graph.view.translate.y != autoDy)) {
						this.autoTranslate = true;

						// NOTE: THIS INVOKES THIS METHOD AGAIN. UNFORTUNATELY
						// THERE IS NO WAY AROUND THIS SINCE THE BOUNDS ARE
						// KNOWN AFTER THE VALIDATION AND SETTING THE
						// TRANSLATE TRIGGERS A REVALIDATION. SHOULD
						// MOVE TRANSLATE/SCALE TO VIEW.
						var tx = graph.view.translate.x;
						var ty = graph.view.translate.y;

						graph.view.setTranslate(autoDx, autoDy);
						graph.container.scrollLeft += (autoDx - tx)
								* graph.view.scale;
						graph.container.scrollTop += (autoDy - ty)
								* graph.view.scale;

						this.autoTranslate = false;
						return;
					}
				} else {
					graph.minimumGraphSize = null;
				}

				graphSizeDidChange.apply(this, arguments);
			}
		};

		// LATER: Cleanup
		graph.getPreferredPageSize = function(bounds, width, height) {
			var border = this.getBorder();
			var t = this.view.translate;
			var s = this.view.scale;
			width = Math.max(0, bounds.x + bounds.width + border - t.x * s);
			height = Math.max(0, bounds.y + bounds.height + border - t.y * s);
			var fmt = this.pageFormat;
			var ps = this.pageScale;
			var page = new mxRectangle(0, 0, fmt.width * ps, fmt.height * ps);

			var hCount = (this.pageBreaksVisible) ? Math.max(1, Math.ceil(width
					/ (page.width * s))) : 1;
			var vCount = (this.pageBreaksVisible) ? Math.max(1, Math
					.ceil(height / (page.height * s))) : 1;
			var gb = this.getGraphBounds();

			// Computes unscaled, untranslated graph bounds
			var x = (gb.width > 0) ? gb.x / this.view.scale
					- this.view.translate.x : 0;
			var y = (gb.height > 0) ? gb.y / this.view.scale
					- this.view.translate.y : 0;
			var w = gb.width / this.view.scale;
			var h = gb.height / this.view.scale;

			var fmt = this.pageFormat;
			var ps = this.pageScale;

			var pw = fmt.width * ps;
			var ph = fmt.height * ps;

			var x0 = Math.floor(Math.min(0, x) / pw);
			var y0 = Math.floor(Math.min(0, y) / ph);

			hCount -= x0;
			vCount -= y0;

			return new mxRectangle(0, 0, hCount * page.width + 2, vCount
					* page.height + 2);
		};

		// LATER: Zoom to multiple pages using minimumGraphSize
		var outlineGetSourceContainerSize = this.editor.outline.getSourceContainerSize;
		this.editor.outline.getSourceContainerSize = function() {
			if (graph.scrollbars && !touchStyle) {
				var scale = this.source.view.scale;

				return new mxRectangle(0, 0, this.source.container.scrollWidth
						- pageBorder * 2 * scale,
						this.source.container.scrollHeight - pageBorder * 2
								* scale);
			}

			return outlineGetSourceContainerSize.apply(this, arguments);
		};

		this.editor.outline.getOutlineOffset = function(scale) {
			if (graph.scrollbars && !touchStyle) {
				var fmt = this.source.pageFormat;
				var ps = this.source.pageScale;

				var pw = fmt.width * ps;
				var ph = fmt.height * ps;

				var dx = this.outline.container.clientWidth / scale - pw;
				var dy = this.outline.container.clientHeight / scale - ph;

				return new mxPoint(dx / 2 - pageBorder, dy / 2 - pageBorder);
			}

			return null;
		};

		graph.sizeDidChange();

		// Sets the default edge
		var defaultEdge = new mxCell('', new mxGeometry(0, 0, 0, 0),
				'endArrow=none');
		defaultEdge.geometry.relative = true;
		defaultEdge.edge = true;

		graph.setDefaultEdge(defaultEdge);

		// Switch to page view by default
		this.actions.get('pageView').funct();

		var showIntegrationUi = typeof (driveIntegration) === 'undefined' ? true
				: driveIntegration;

		if ((typeof (gapi) != 'undefined') && showIntegrationUi) {
			this.menubar.container.appendChild(this.createIntegrationUi());
		}

		/*
		 * setInterval(mxUtils.bind(this, function() { this.checkSession(); }),
		 * 1000);
		 */
	};

	/**
	 * Returns the URL for a copy of this editor with no state.
	 */
	EditorUi.prototype.getUrl = function(pathname) {
		var href = (pathname != null) ? pathname : window.location.pathname;
		var parms = (href.indexOf('?') > 0) ? 1 : 0;

		// Removes template URL parameter for new blank diagram
		for ( var key in urlParams) {
			if (key != 'tmp' && key != 'libs' && key != 'state'
					&& key != 'fileId' && key != 'code' && key != 'share'
					&& key != 'url') {
				if (parms == 0) {
					href += '?';
				} else {
					href += '&';
				}

				href += key + '=' + urlParams[key];
				parms++;
			}
		}

		return href;
	};

	// Loads the specified template
	var editorUiOpen = EditorUi.prototype.open;
	EditorUi.prototype.open = function() {
		// Cross-domain window access is not allowed in FF, so if we
		// were opened from another domain then this will fail.
		var openingFile = false;

		try {
			openingFile = !(window.opener == null || window.opener.openFile == null);
		} catch (e) {
			// ignore
		}

		if (!openingFile) {
			// Checks if we should connect to a shared diagram
			var documentName = urlParams['share'];
			var urlParam = urlParams['url'];

			if (documentName != null) {
				this.connect(documentName);
			} else if (urlParam != null) {
				// Loads diagram from the given URL
				var spinner = mxIntegration
						.createSpinner(this.editor.graph.container);
				this.editor.setStatus(mxResources.get('loading') + '...');

				mxUtils
						.get(
								'proxy?url=' + urlParam,
								mxUtils
										.bind(
												this,
												function(req) {
													spinner.stop();

													if (req.getStatus() != 200) {
														this.editor
																.setStatus(mxResources
																		.get('fileNotLoaded'));
														mxUtils
																.alert(mxResources
																		.get('fileNotLoaded'));
													} else {
														var text = req
																.getText();

														if (text != null
																&& text.length > 0) {
															var doc = mxUtils
																	.parseXml(text);
															this.editor
																	.setGraphXml(doc.documentElement);

															// Restores initial
															// diagram state
															this.editor.modified = false;
															this.editor.undoManager
																	.clear();
															this.editor.filename = null;

															this.editor
																	.setStatus('');
															this.editor.graph.container
																	.focus();
														} else {
															this.editor
																	.setStatus(mxResources
																			.get('fileNotLoaded'));
															mxUtils
																	.alert(mxResources
																			.get('fileNotLoaded'));
														}
													}
												}), function() {
									spinner.stop();
									this.editor.setStatus(mxResources
											.get('errorLoadingFile'));
									mxUtils.alert(mxResources
											.get('errorLoadingFile'));
								});
			}

			// Opens the given template
			var template = urlParams['tmp'];

			if (template != null && template.length > 0) {
				mxUtils.get(TEMPLATE_PATH + '/xml/' + template + '.xml',
						mxUtils.bind(this, function(req) {
							this.editor.setGraphXml(req.getDocumentElement());

							// Restores initial diagram state
							this.editor.modified = false;
							this.editor.undoManager.clear();
						}));
			}
		} else {
			editorUiOpen.apply(this, arguments);
		}
	};

	EditorUi.prototype.save = function(name, saveAs) {
		var editorUi = this;

		if (name == null) {
			return;
		}
		// console.log(this.editor.graph.getModel());
		var xml = mxUtils.getXml(this.editor.getGraphXml());
		xml = "<mxGraphModel>" + xml + "</mxGraphModel>";

		if (!mxIntegration.loggedOut && mxGoogleDrive.isDriveReady()) {
			// console.log("GD");
			if (urlParams['state'] != null) {
				var tmp = JSON.parse(decodeURIComponent(urlParams['state']));
				if (tmp != null && tmp.folderId != null
						&& tmp.folderId.length > 0) {
					mxGoogleDrive.fileInfo.parents = [ {
						'kind' : 'drive#fileLink',
						'id' : tmp.folderId
					} ];
				}
			}
			mxGoogleDrive.stateMachine.save(saveAs ? null
					: mxGoogleDrive.fileInfo.id,
					mxGoogleDrive.fileInfo.parents, name, xml);
			this.editor.filename = name;
		} else if (useLocalStorage) {
			// console.log("LS");
			if (localStorage.getItem(name) != null
					&& !mxUtils.confirm(mxResources.get('replace', [ name ]))) {
				return;
			}

			localStorage.setItem(name, xml);
			this.editor.setStatus(mxResources.get('saved') + ' ' + new Date());

			this.editor.filename = name;
			this.editor.modified = false;
		} else {
			// console.log("Normale?");
			if (xml.length < MAX_REQUEST_SIZE) {
				xml = encodeURIComponent(xml);
				new mxXmlRequest(SAVE_URL, 'filename=' + name + '&xml=' + xml)
						.simulate(document, "_blank");
			} else {
				mxUtils.alert(mxResources.get('drawingTooLarge'));
				mxUtils.popup(xml);

				return;
			}

			this.editor.filename = name;
			this.editor.modified = false;
		}

	}

	EditorUi.prototype.saveInput = function(name, saveAs) {
		var editorUi = this;

		if (name == null) {
			return;
		}
		// console.log(this.editor.graph.getModel());
		
		xmlDoc = createXMLDiagram(this.editor.graph);
		
		var enc = new mxCodec();
		var node = enc.encode(xmlDoc.documentElement);

		xml = mxUtils.getXml(node);

		// var xml = mxUtils.getXml(this.editor.getGraphXml());

		if (!mxIntegration.loggedOut && mxGoogleDrive.isDriveReady()) {
			// console.log("GD");
			if (urlParams['state'] != null) {
				var tmp = JSON.parse(decodeURIComponent(urlParams['state']));
				if (tmp != null && tmp.folderId != null
						&& tmp.folderId.length > 0) {
					mxGoogleDrive.fileInfo.parents = [ {
						'kind' : 'drive#fileLink',
						'id' : tmp.folderId
					} ];
				}
			}
			mxGoogleDrive.stateMachine.save(saveAs ? null
					: mxGoogleDrive.fileInfo.id,
					mxGoogleDrive.fileInfo.parents, name, xml);
			this.editor.filename = name;
		} else if (useLocalStorage) {
			// console.log("LS");
			if (localStorage.getItem(name) != null
					&& !mxUtils.confirm(mxResources.get('replace', [ name ]))) {
				return;
			}

			localStorage.setItem(name, xml);
			this.editor.setStatus(mxResources.get('saved') + ' ' + new Date());

			this.editor.filename = name;
			this.editor.modified = false;
		} else {
			// console.log("Normale?");
			if (xml.length < MAX_REQUEST_SIZE) {
				xml = encodeURIComponent(xml);
				new mxXmlRequest(SAVE_URL, 'filename=' + name + '&xml=' + xml)
						.simulate(document, "_blank");
			} else {
				mxUtils.alert(mxResources.get('drawingTooLarge'));
				mxUtils.popup(xml);

				return;
			}

			this.editor.filename = name;
			this.editor.modified = false;
		}

	}

	//EditorUi.colorSuccess = "strokeColor=#5cb85c;";
	
	EditorUi.colorSuccess = "strokeColor=#000000;"
	EditorUi.colorError = "strokeColor=#d43f3a;";

	EditorUi.setColorElement = function(style, color) {
		var styleColor = "";
		var i;
		var entra = true;
		var array = new Array();

		var str = "" + style;
		var res = str.split(";");
		var lunC = res.length;

		for (i = 0; i < lunC; i++) {
			var array = new Array();
			array = res[i].split("=");
			if (array[0] == "strokeColor") {
				entra = false;
				styleColor += color;
			} else {
				styleColor += res[i] + ";";
			}
		}
		if (entra) {
			styleColor += color;
		}
		return styleColor;

	}


	var mappaRis= new Object();
	function colorDiagram (req) {
		//console.log(req.getText());
		var obj = JSON.parse(req.getText());
		var rs = obj.Result;
		var ln = rs.length;
		mappaRis= new Object();
		
		/*console.log("------------ Con ");
		console.log(mappaConnectors);
		console.log("------------ Syn ");
		console.log(mappaSymbols);
		console.log("------------ Res ");
		console.log(mappaRis);
		console.log("------------ Syn ");*/
		
		for (var i=0;i<ln;i++) {
			if (mappaConnectors[rs[i].Key]!=undefined){
				mappaRis[rs[i].Key] = true;
			}
			if (mappaSymbols[rs[i].Key]!=undefined) {
				mappaRis[rs[i].Key] = true;
			}
						
			aggiungiTitolo(rs[i].Key,rs[i].Msg);
			aggiungiDescrizione(rs[i].Key,rs[i].Error);	
		}
		
		var lunRis = Object.keys(mappaRis).length; 
		if (lunRis == 0){
			mappaRis["Clear"]=true;
		}
	}
	
	function colorDiagramComplete (req) {
		//console.log(req.getText());
		var obj = JSON.parse(req.getText());
		var rs = obj.Result;
		var ln = rs.length;
		mappaRis= new Object();
	
		for (var i=0;i<ln;i++) {
			mappaRis[rs[i].Key] = true;
			
			aggiungiTitolo(rs[i].Key,rs[i].Msg);
			aggiungiDescrizione(rs[i].Key,rs[i].Error);	
		}
		
		var lunRis = Object.keys(mappaRis).length; 
		if (lunRis == 0){
			mappaRis["Clear"]=true;
		}
	}
	
	function loadText (req){
		console.log(req.getText());
	}

	var mappaSymbols = new Object();
	var mappaConnectors = new Object();
	var clearGraph = 1;
	
	EditorUi.prototype.interactiveInputDelete = function(graph) {
		
//		function removeSymbol(symbol) {
//			clearConsole();
//			var id = "S_" + symbol.id;
//			
//			//console.log("Eliminato il Simbolo: " + id);
//			delete mappaSymbols[id];
//			new mxXmlRequest(INTERACTIVE_URL,'Op=removeSymbol&id='+id, 'POST', false).send(colorDiagram);
//		}
//
//		function removeConnector(connector) {
//			clearConsole();
//			var id = "C_" + connector.id;
//			
//			//console.log("Eliminato il Connettore: " + id);
//			delete mappaConnectors[id];
//			new mxXmlRequest(INTERACTIVE_URL,'Op=removeConnector&id='+id, 'POST', false).send(colorDiagram);
//		}
//				
//		var model = graph.getModel();
//		for ( var item in model.cells) {
//			if (model.cells[item].vertex) {
//				mappaSymbols["S_"+model.cells[item].id].check = true;
//			}
//			if (model.cells[item].edge) {
//				mappaConnectors["C_"+model.cells[item].id].check = true;
//			}
//		}
//						
//		for(var key in mappaSymbols) {
//			var sym = mappaSymbols[key];
//			if (sym.check) {
//				sym.check = false;
//			} else {
//				removeSymbol(sym.rif);
//			}
//		}
//				
//		for(var key in mappaConnectors) {
//			var con = mappaConnectors[key];
//			if (con.check) {
//				con.check = false;
//			} else {
//				removeConnector(con.rif);
//			}
//		}
//		
//		colorGraph(graph);
//		
////		console.log(" *** DOPO *** ");
////		console.log(mappaSymbols);
////		console.log(" *** DOPO *** ");
////		console.log(mappaConnectors);
////		console.log(" ************ ");		
	}

	EditorUi.prototype.interactiveInputAddUpdate = function(graph) {
		
//		function addSymbol(symbol) {
//			clearConsole();
//			
//			var id = "S_" + symbol.id;
//			var graphicRef = symbol.value.getAttribute("graphicRef");
//			var sym = {rif:symbol, check:false};
//				
//			//console.log("Aggiunto un nuovo Simbolo:" + id);
//			mappaSymbols[id] = sym;		
//			
//			new mxXmlRequest(INTERACTIVE_URL,'Op=addSymbol&id='+id+'&graphicRef='+graphicRef, 'POST', false).send(colorDiagram);			
//			return;
//		}
//		
//		function addConnector(connector, style) {
//			clearConsole();
//			
//			var id = "C_" + connector.id;
//			var graphicRef = style.graphicRef;
//			var conn = {rif:connector, check:false ,source:null,sourceapp:null, target:null,targetapp:null};
//						
//			//console.log("Aggiunto un nuovo Connettore: " + id);
//			mappaConnectors[id] = conn;
//		
//			new mxXmlRequest(INTERACTIVE_URL,'Op=addConnector&id='+id+'&graphicRef='+graphicRef, 'POST', false).send(colorDiagram);
//		    return;
//		}
//		
//		function addConnection(graph,symbol,connector,isTarget) {
//			clearConsole();
//			
//			var idC = "C_" + connector.id;
//			var idS = "S_" + symbol.id;
//			
//			//console.log("Aggiunto un nuovo Attacching Point: " + idS +" - "+idC );
//						
//			var value = symbol.value;
//			var states = graph.getView().getCellStates(new Array(symbol, connector));
//			var connP = graph.getConnectionConstraint(states[1],states[0], !isTarget);
//			var graphicRef = "Border";
//			if (connP.point != null) {
//				var conns = value.getElementsByTagName("connections")[0].getElementsByTagName("constraint");
//				for (x = 0; x < conns.length; x++) {
//					if (connP.point.x == conns[x].getAttribute("x")	&& connP.point.y == conns[x].getAttribute("y")) {
//						graphicRef = conns[x].getAttribute("name");
//					}
//				}
//			} 
//			//console.log("graphicRef: " + graphicRef);
//
//			var edgSt = graph.getCellStyle(connector);
//			var edgAtP = edgSt.attP;
//			var attacchingPoints = edgAtP.split("-");
//			var connRef="";
//
//			if (edgAtP.indexOf(":") > -1) {
//				connRef = attacchingPoints[0].split(":")[isTarget];
//				
//				//console.log("connRef: " + idC + "." + connRef);
//			} else {
//				connRef = attacchingPoints[isTarget];
//				//console.log("connRef: " + idC + "." + attacchingPoints[isTarget]);
//			}
//			
//			var app = {graphicRef:graphicRef,connRef:connRef};
//			if (isTarget) {
//				mappaConnectors[idC].target=symbol;
//				mappaConnectors[idC].targetapp=app;
//			} else {
//				mappaConnectors[idC].source = symbol;
//				mappaConnectors[idC].sourceapp=app;
//			}
//			
//			new mxXmlRequest(INTERACTIVE_URL,'Op=addConnection&idS='+idS +'&idC='+idC +'&graphicRef='+graphicRef+'&connRef='+connRef, 'POST', false).send(colorDiagram);
//		    return;
//		}
//		
//		function removeConnection(idSymbol,idConnector,isTarget) {
//			clearConsole();
//			
//			var idC = "C_" + idConnector;
//			var idS = "S_" + idSymbol;
//			var connRef="";
//			var graphicRef = "";
//			
//			//console.log("Rimosso un Attacching Point: " + idS +" - "+idC );
//			if (isTarget) {
//				mappaConnectors[idC].target=null;
//				var app = mappaConnectors[idC].targetapp;
//				connRef = app.connRef;
//				graphicRef = app.graphicRef;
//				mappaConnectors[idC].targetapp = null;
//			} else {
//				mappaConnectors[idC].source = null;
//				var app = mappaConnectors[idC].sourceapp;
//				connRef = app.connRef;
//				graphicRef = app.graphicRef;
//				mappaConnectors[idC].sourceapp = null;
//			}
//											
//			new mxXmlRequest(INTERACTIVE_URL,'Op=removeConnection&idS='+idS +'&idC='+idC +'&graphicRef='+graphicRef+'&connRef='+connRef, 'POST', false).send(colorDiagram);
//		    return;
//		}
//		
//		if (clearGraph){
//			new mxXmlRequest(INTERACTIVE_URL,'Op=parserXMLDefinition', 'POST', false).send(loadText);
//			clearGraph = 0;
//		}
//		
//		
//		/* Ci possono essere inserimenti di più oggetti*/
//		var model = graph.getModel();
//		for ( var item in model.cells) {
//			var ele = model.cells[item];
//			
//			if (ele.vertex) {
//				clearBorderDiv("S_"+ele.id);
//			} else if (ele.edge) {
//				clearBorderDiv("C_"+ele.id);
//			} 
//			
//			if (ele.vertex && mappaSymbols["S_"+ele.id]==undefined) {
//				addSymbol(ele);
//			} else if (ele.edge && mappaConnectors["C_"+ele.id]==undefined) {
//				addConnector(ele, graph.getCellStyle(ele));
//			} 
//			
//			if (ele.edge && mappaConnectors["C_"+ele.id].source!=ele.source) {
//				if (ele.source==null){
//					removeConnection(mappaConnectors["C_"+ele.id].source.id,ele.id,0);
//				}else {
//					addConnection(graph,ele.source,ele,0);
//				}
//			} 
//			
//			if (ele.edge && mappaConnectors["C_"+ele.id].target!=ele.target) {
//				if (ele.target==null){
//					removeConnection(mappaConnectors["C_"+ele.id].target.id,ele.id,1);
//				}else {
//					addConnection(graph,ele.target,ele,1);
//				}	
//			}
//			
//		}
//		
//		//console.log(graph.getSelectionCells());
//		
//		var modelSelected = graph.getSelectionCells();
//		var lunSelezione = Object.keys(modelSelected).length;
//	
//		for ( var k=0;k<lunSelezione;k++) {
//			var eleS = modelSelected[k];
//			if (eleS.vertex) {
//				colorBorderDiv("S_"+eleS.id);
//			} else if (eleS.edge) {
//				colorBorderDiv("C_"+eleS.id);
//			} 
//		}
//		
//		colorGraph(graph);
		
	}
	
	EditorUi.prototype.completeInputAddUpdate = function(graph) {
		
		/* Ci possono essere inserimenti di più oggetti*/
		var model = graph.getModel();
		for ( var item in model.cells) {
			var ele = model.cells[item];
			
			if (ele.vertex) {
				clearBorderDiv("S_"+ele.id);
			} else if (ele.edge) {
				clearBorderDiv("C_"+ele.id);
			} 
						
		}
		
		var modelSelected = graph.getSelectionCells();
		var lunSelezione = Object.keys(modelSelected).length;
		
		for ( var k=0;k<lunSelezione;k++) {
			var eleS = modelSelected[k];
			if (eleS.vertex) {
				colorBorderDiv("S_"+eleS.id);
			} else if (eleS.edge) {
				colorBorderDiv("C_"+eleS.id);
			} 
		}
		
		colorGraph(graph);
	}
	
	function colorGraph(graph){
		var lunRis = Object.keys(mappaRis).length; 
				
		if (lunRis > 0) {
			//console.log(Object.keys(mappaRis));
			var styleColor; 
			var modelRis = graph.getModel(); 
			var idRis = "";
			for (var item in modelRis.cells) { 
				styleColor = ""; 
				if(modelRis.cells[item].vertex) {
					idRis = "S_"+modelRis.cells[item].id;
				} else if(modelRis.cells[item].edge) {  
					idRis = "C_"+modelRis.cells[item].id;
				}
			
				if (mappaRis[idRis]==undefined){
					styleColor = EditorUi.setColorElement(modelRis.cells[item].style,EditorUi.colorSuccess);
				} else {
					styleColor = EditorUi.setColorElement(modelRis.cells[item].style,EditorUi.colorError);
				}
			
				graph.getModel().setStyle(modelRis.cells[item], styleColor); 
			}
			mappaRis = new Object();
		}
	}
	
	function createXMLDiagram(graph){
		var model = graph.getModel();
		var symbols = new Array();
		var connectors = new Array();
		for ( var item in model.cells) {
			if (model.cells[item].vertex) {
				symbols.push(model.cells[item]);
			} else if (model.cells[item].edge) {
				connectors.push(model.cells[item]);
			}
		}

		xmlDoc = document.implementation.createDocument("", "", null);
		fakeRoot = xmlDoc.createElement("fake");
		root = xmlDoc.createElement("language");
		fakeRoot.appendChild(root);
		xmlDoc.appendChild(fakeRoot);
		cellStates = graph.getView().getStates().getValues();
		var grp = graph;

		for (var j = 0; j < symbols.length; j++) {
			var sym = symbols[j];
			var value = sym.value;
			
			elem = xmlDoc.createElement("symbol");
			elem.setAttribute("id", "S_" + sym.id);
			elem.setAttribute("graphicRef", value.getAttribute("graphicRef"));

			if (sym.edges != null) {
				for (k = 0; k < sym.edges.length; k++) {

					if (sym == sym.edges[k].source) {
						attP = xmlDoc.createElement("ap");
						elem.appendChild(attP);
						
						// console.log("Is source");
						/*
						 * console.log(cellStates.get(sym.edges[k].mxObjectId));
						 * console.log(sym.mxObjectId); for(y=0;y<cellStates.length;y++) {
						 * if(cellStates[y].cell==sym) {
						 * console.log(cellStates[y]);
						 * vertexState=cellStates[y]; console.log(vertexState); }
						 * if(cellStates[y].cell==sym.edges[k]) {
						 * console.log(cellStates[y]); edgeState=cellStates[y];
						 * console.log(edgeState); } }
						 */
						
						states = grp.getView().getCellStates(
								new Array(sym, sym.edges[k]));
						connP = grp.getConnectionConstraint(states[1],
								states[0], true);
						// connP =
						// grp.getConnectionConstraint(edgeState,vertexState,true);
						if (connP.point != null) {
							var conns = value
									.getElementsByTagName("connections")[0]
									.getElementsByTagName("constraint");
							for (x = 0; x < conns.length; x++) {
								if (connP.point.x == conns[x].getAttribute("x")
										&& connP.point.y == conns[x]
												.getAttribute("y")) {
									nameAp = conns[x].getAttribute("name");
								}
							}
						} else
							nameAp = "Border";
						attP.setAttribute("graphicRef", nameAp);
						edgSt = grp.getCellStyle(sym.edges[k]);
						edgAtP = edgSt.attP;

						if (edgAtP.indexOf(":") > -1) {
							attacchingPoints = edgAtP.split("-");
							attPoint = attacchingPoints[0].split(":")[0];
							attP.setAttribute("connRef", "C_" + sym.edges[k].id
									+ "." + attPoint);
						} else {
							attacchingPoints = edgAtP.split("-");
							attP.setAttribute("connRef", "C_" + sym.edges[k].id
									+ "." + attacchingPoints[0]);
						}
						/*
						 * if(edgSt.endArrow == undefined) {
						 * attP.setAttribute("connRef","C_"+sym.edges[k].id+".P1"); }
						 * else {
						 * attP.setAttribute("connRef","C_"+sym.edges[k].id+".Tail"); }
						 */
						// console.log(grp.getCellStyle(sym.edges[k]).endArrow);
						/* Controllo per identificare il nome */
					}
					if (sym == sym.edges[k].target) {
						attP = xmlDoc.createElement("ap");
						elem.appendChild(attP);
						
						// console.log("Is target");
						/*
						 * for(y=0;y<cellStates.length;y++) {
						 * if(cellStates[y].cell==sym) {
						 * console.log(cellStates[y]);
						 * vertexState=cellStates[y]; console.log(vertexState); }
						 * if(cellStates[y].cell==sym.edges[k]) {
						 * console.log(cellStates[y]); edgeState=cellStates[y];
						 * console.log(edgeState); } }
						 */

						states = grp.getView().getCellStates(
								new Array(sym, sym.edges[k]));
						connP = grp.getConnectionConstraint(states[1],
								states[0], false);						
						if (connP.point != null) {
							var conns = value
									.getElementsByTagName("connections")[0]
									.getElementsByTagName("constraint");
							for (x = 0; x < conns.length; x++) {
								if (connP.point.x == conns[x].getAttribute("x")
										&& connP.point.y == conns[x]
												.getAttribute("y")) {
									nameAp = conns[x].getAttribute("name");									
								}
							}
						} else
							nameAp = "Border";
						
						attP.setAttribute("graphicRef", nameAp);
						edgSt = grp.getCellStyle(sym.edges[k]);
						edgAtP = edgSt.attP;
						if (edgAtP.indexOf(":") > -1) {
							attacchingPoints = edgAtP.split("-");
							attPoint = attacchingPoints[0].split(":")[1];
							attP.setAttribute("connRef", "C_" + sym.edges[k].id
									+ "." + attPoint);
						} else {
							attacchingPoints = edgAtP.split("-");
							attP.setAttribute("connRef", "C_" + sym.edges[k].id
									+ "." + attacchingPoints[1]);
						}
						/*
						 * if(edgSt.endArrow == undefined) {
						 * attP.setAttribute("connRef","C_"+sym.edges[k].id+".P2"); }
						 * else {
						 * attP.setAttribute("connRef","C_"+sym.edges[k].id+".Head"); }
						 */
					}	
				}
			}
			
			var graphicRefKeyText = value.getAttribute("graphicRef").toLowerCase();
			
			if (mappaRefSemantic[graphicRefKeyText] != undefined) {
				appText = xmlDoc.createElement("aptext");
				appText.setAttribute("graphicRef", "Center");
				if ((sym.getAttribute('label', '')=="") || (sym.getAttribute('label', '')==TEXTDEFAULT)) {
					appText.setAttribute("value", "S_" + sym.id);					
				} else {
					var risStr = sym.getAttribute('label', '').replace("<p>","").replace("</p>","");
					appText.setAttribute("value", risStr);
				}
				elem.appendChild(appText);
			}
			
			root.appendChild(elem);
		}

		for (i = 0; i < connectors.length; i++) {
			con = connectors[i];
			style = grp.getCellStyle(con);
			connector = xmlDoc.createElement("connector");
			connector.setAttribute("id", "C_" + con.id);
			if (style.endArrow == undefined) {
				/* Da gestire */
				connector.setAttribute("graphicRef", style.graphicRef);
			} else {
				/* Da gestire */
				connector.setAttribute("graphicRef", style.graphicRef);
				// attP.setAttribute("connRef","C_"+sym.edges[k].id+".Head");
			}
			
			graphicRefKeyText=style.graphicRef.toLowerCase();
			if (con.value.length>0) {

					
				if ((con.value!=TEXTDEFAULT) && (mappaRefSemantic[graphicRefKeyText] != undefined)) {
					var exp = con.value.replace(/\s*/g,"");
					if (exp.length>0){
						appText = xmlDoc.createElement("aptext");
						appText.setAttribute("graphicRef", "Center");
						appText.setAttribute("value", con.value.replace("<p>","").replace("</p>",""));
						connector.appendChild(appText);
					}
				} 
			}
			
			root.appendChild(connector);
		}

		return xmlDoc;
		
	}
	
	
	EditorUi.prototype.test = function(graph) {
		var editorUi = this;

		if (name == null) {
			return;
		}
		
		xmlDoc = createXMLDiagram(this.editor.graph);
		var enc = new mxCodec();
		var node = enc.encode(xmlDoc.documentElement);

		xml = mxUtils.getXml(node);

		// var xml = mxUtils.getXml(this.editor.getGraphXml());

		// console.log("Normale?");
		if (xml.length < MAX_REQUEST_SIZE) {
			xml = encodeURIComponent(xml);
						
			var onload = function(req) {
				
				aggiungiTitolo("Test","Test");
				aggiungiDescrizione("Test",req.getText());
			}
			new mxXmlRequest(INTERACTIVE_URL,'Op=test&xml=' + xml, 'POST', false).send(onload);
			//this.savePreview();
		} else {
			mxUtils.alert(mxResources.get('drawingTooLarge'));
			mxUtils.popup(xml);

			return;
		}


	}

	
	

	EditorUi.prototype.checkInput = function() {
		var editorUi = this;

		if (name == null) {
			return;
		}
		
		xmlDoc = createXMLDiagram(this.editor.graph);
		var enc = new mxCodec();
		var node = enc.encode(xmlDoc.documentElement);

		xml = mxUtils.getXml(node);

		// var xml = mxUtils.getXml(this.editor.getGraphXml());

		// console.log("Normale?");
		if (xml.length < MAX_REQUEST_SIZE) {
			xml = encodeURIComponent(xml);
			clearConsole();
			if(localStorage.getItem("RULES")!=null){
				$.post( "rulesgen", { n1: localStorage.getItem("RULES")})
					.done();
			}
			if(localStorage.getItem("SEMANTIC_RULES")!=null){
				$.post( "semanticgen", { n1: localStorage.getItem("SEMANTIC_RULES")})
					.done();
			}
			new mxXmlRequest(CHECK_URL, 'xml=' + xml, 'POST', false).send(colorDiagramComplete);
			colorGraph(this.editor.graph);
			this.savePreview();
		} else {
			mxUtils.alert(mxResources.get('drawingTooLarge'));
			mxUtils.popup(xml);

			return;
		}

		this.editor.filename = name;
		this.editor.modified = false;

	}

	// Sharing
	/*
	 * EditorUi.prototype.connect = function(name, highlight) { if (this.sharing ==
	 * null) { this.editor.setStatus(mxResources.get('connecting') + '...');
	 * 
	 * try { sharejs.open(name, 'json', SHARE_HOST + '/sjs', mxUtils.bind(this,
	 * function(error, doc, connection) { if (doc == null) {
	 * mxUtils.alert(error); } else { this.sharing = new
	 * Sharing(this.editor.graph.getModel(), doc);
	 * this.editor.undoManager.clear(); var url = this.getSharingUrl();
	 *  // Together with the overridden hook below, this allows // selection
	 * inside the input that shows the share URL. It // also allows context menu
	 * for copy paste, deselects when the // focus is lost and selects all if
	 * the mouse is clicked // inside the input element. var select = 'var
	 * text=document.getElementById(\'shareUrl\');text.style.backgroundColor=\'\';text.focus();text.select();if(window.event!=null){window.event.cancelBubble=true;}return
	 * false;'; var handlers = 'onmousedown="' + select + '" onclick="' + select +
	 * '"';
	 * 
	 * if (mxClient.IS_IE && mxClient.IS_SVG) { handlers += '
	 * onblur="document.selection.empty();"'; } else if (mxClient.IS_IE) {
	 * handlers += ' onmouseup="' + select + '"'; }
	 * 
	 * var style = 'color:gray;border:0px;margin:0px;';
	 * 
	 * if (highlight) { style += 'background-color:yellow;'; }
	 * 
	 * var url = this.getSharingUrl(); var footer =
	 * document.getElementById('geFooter');
	 * 
	 * this.editor.setStatus('<input id="shareUrl" style="' + style + '"
	 * type="text" size="50" ' + 'value="' + url + '" readonly ' + handlers + '
	 * title="' + mxResources.get('shareLink') + '"/>');
	 * 
	 * connection.on('disconnect', mxUtils.bind(this, function() {
	 * this.disconnect();
	 * this.editor.setStatus(mxResources.get('notConnected')); })); } })); }
	 * catch (err) { mxUtils.alert(err); } } };
	 * 
	 * var editorUiIsSelectionAllowed = EditorUi.prototype.isSelectionAllowed;
	 * EditorUi.prototype.isSelectionAllowed = function(evt) { var txt =
	 * document.getElementById('shareUrl');
	 * 
	 * if (txt != null && mxEvent.getSource(evt) == txt) { return true; }
	 * 
	 * return editorUiIsSelectionAllowed.apply(this, arguments); };
	 */

	// Currently not available via UI
	EditorUi.prototype.disconnect = function() {
		if (this.sharing != null) {
			this.editor.setStatus('');
			this.sharing.doc.close();
			this.sharing.destroy();
			this.sharing = null;
		}
	};

	EditorUi.prototype.getSharingUrl = function() {
		if (this.sharing != null) {
			var port = (window.location.port != '' && window.location.port != '80') ? (':' + window.location.port)
					: '';
			var host = window.location.hostname;

			if (host == 'drive.diagram.ly') {
				host = 'www.draw.io';
			}

			return this.getUrl(window.location.protocol + '//' + host + port
					+ window.location.pathname + '?share='
					+ this.sharing.doc.name);
		}

		return null;
	};

	// Replaces save button if alternative I/O is available (Chrome Dev-Channel
	// or Flash)
	EditorUi.prototype.replaceSaveButton = function(elt, dataCallback,
			filenameCallback, onComplete) {
		var result = null;
		var wnd = window;
		wnd.URL = wnd.webkitURL || wnd.URL;
		wnd.BlobBuilder = wnd.BlobBuilder || wnd.WebKitBlobBuilder
				|| wnd.MozBlobBuilder;

		// Prefers BLOB Builder API in Chrome
		/*
		 * if (mxClient.IS_GC && (wnd.URL != null && wnd.BlobBuilder != null)) { //
		 * Experimental Chrome feature result =
		 * mxUtils.button(mxResources.get('save'), mxUtils.bind(this, function() {
		 * var bb = new wnd.BlobBuilder(); bb.append(dataCallback());
		 * 
		 * var a = wnd.document.createElement('a'); a.download =
		 * filenameCallback(); a.href =
		 * wnd.URL.createObjectURL(bb.getBlob('text/plain'));
		 * a.dataset.downloadurl = ['text/plain', a.download, a.href].join(':');
		 * 
		 * var evt = document.createEvent("MouseEvents");
		 * evt.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false,
		 * false, false, false, 0, null); var allowDefault =
		 * a.dispatchEvent(evt); onComplete(); }));
		 * 
		 * elt.parentNode.replaceChild(result, elt); } else
		 */
		// FIXME:
		// - Possible to hover over button in IE (near right border)
		// - Removes focus from input element while entering filename
		// - No full hover simulation possible, only focus on button
		if (typeof (swfobject) != 'undefined'
				&& swfobject.hasFlashPlayerVersion("10")
				&& typeof (Downloadify) != 'undefined') {
			result = document.createElement('span');
			elt.parentNode.insertBefore(result, elt);

			// Adds a Flash object as a button
			Downloadify.create(result, {
				data : dataCallback,
				filename : filenameCallback,
				onComplete : onComplete,
				onCancel : function() {
				},
				onError : function() {
				},
				swf : 'js/downloadify/downloadify.swf',
				downloadImage : 'js/downloadify/transparent.png',
				width : elt.offsetWidth + 2,
				height : elt.offsetHeight + 2,
				transparent : true,
				append : true
			});

			// Fixes vertical shift of OBJECT node
			var dx = '-6px';

			if (mxClient.IS_IE && document.documentMode == 9) {
				dx = '-7px';
			} else if (mxClient.IS_IE) {
				dx = '-3px';
			}

			result.style.display = 'inline';
			result.style.position = 'absolute';
			result.style.left = (elt.offsetLeft + 20) + 'px';
			result.style.height = (elt.offsetHeight + 2) + 'px';
			result.style.width = (elt.offsetWidth + 2) + 'px';
			result.firstChild.style.marginBottom = dx;

			mxEvent.addListener(result, 'mouseover', function(evt) {
				elt.focus();
			});

			mxEvent.addListener(result, 'mouseout', function(evt) {
				elt.blur();
			});
		}

		return result;
	};

	// Extends Save Dialog to replace Save button
	if (!useLocalStorage) {
		EditorUi.prototype.saveFile = function(forceDialog) {
			// Required to use new SaveDialog below
			if (!forceDialog && this.editor.filename != null) {
				this.save(this.editor.getOrCreateFilename());
			} else {
				this.showDialog(new SaveDialog(this, forceDialog).container,
						300, 80, true, true);
			}

			// Extends code for using flash in save button
			if (this.dialog != null) {
				// Finds elements inside the current dialog
				var findElt = mxUtils.bind(this, function(tagName) {
					var elts = document.getElementsByTagName(tagName);

					for (var i = 0; i < elts.length; i++) {
						var parent = elts[i].parentNode;

						while (parent != null) {
							if (parent == this.dialog.container) {
								return elts[i];
							}

							parent = parent.parentNode;
						}
					}

					return null;
				});

				// Replaces the Save button
				var input = findElt('input');
				var saveBtn = findElt('button');

				if (input != null && saveBtn != null) {
					this.replaceSaveButton(saveBtn, mxUtils.bind(this,
							function() {
								return mxUtils
										.getXml(this.editor.getGraphXml());
							}), mxUtils.bind(this, function() {
						return input.value;
					}), mxUtils.bind(this, function() {
						this.editor.filename = input.value;
						this.editor.modified = false;
						this.hideDialog();
					}));
				}
			}
		};
	}

})();